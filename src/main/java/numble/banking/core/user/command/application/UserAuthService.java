package numble.banking.core.user.command.application;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BadRequestException;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.common.error.exception.UnAuthorizedException;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthService {

  private static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN-";

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate<String, Object> redisTemplate;

  @Transactional
  public TokenResponse login(LoginRequest request) {

    User user = userRepository.findByLoginId(request.getLoginId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    if(!user.verifyPassword(request.getPassword())) throw new BadRequestException(ErrorCode.WRONG_PASSWORD);

    TokenData tokenData = TokenData.of(user);
    return generateToken(tokenData);
  }

  @Transactional
  public TokenResponse reissue(TokenReissueRequest request) {
    // refresh 토큰 유효한지 검증
    jwtTokenProvider.validateToken(request.getRefreshToken());

    // access 토큰에서 정보 추출
    TokenData tokenData = jwtTokenProvider.getTokenData(request.getAccessToken());
    Long userId = tokenData.getUserId();

    // redis 에서 user id를 통해 저장된 refresh token 추출
    String refreshToken = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);

    // 로그아웃 되었는지 확인
    if(refreshToken == null) throw new UnAuthorizedException(ErrorCode.EXPIRED_VERIFICATION_TOKEN);

    // 입력받은 토큰과 레디스의 토큰 값이 같은지 확인
    if(!refreshToken.equals(request.getRefreshToken())) throw new UnAuthorizedException(ErrorCode.INVALID_VERIFICATION_TOKEN);

    // 레디스에 저장된 refresh token 삭제
    redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);

    // 토큰 새로 생성 후 저장.
    return generateToken(tokenData);
  }

  private TokenResponse generateToken(TokenData tokenData) {

    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);
    String refreshToken = jwtTokenProvider.generateRefreshToken(tokenData);

    saveRefreshTokenInRedis(tokenData.getUserId(), refreshToken);
    return new TokenResponse(accessToken, refreshToken);
  }

  private void saveRefreshTokenInRedis(Long userId, String refreshToken) {
    redisTemplate.opsForValue()
        .set(REFRESH_TOKEN_PREFIX + userId,
            refreshToken,
            jwtTokenProvider.getTokenExpiredIn(refreshToken) - new Date().getTime(),
            TimeUnit.MILLISECONDS);

  }
}
