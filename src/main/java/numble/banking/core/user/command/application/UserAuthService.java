package numble.banking.core.user.command.application;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BadRequestException;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    return generateToken(user);
  }

  private TokenResponse generateToken(User user) {
    TokenData tokenData = TokenData.of(user);

    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);
    String refreshToken = jwtTokenProvider.generateRefreshToken(tokenData);

    saveRefreshTokenInRedis(user, refreshToken);
    return new TokenResponse(accessToken, refreshToken);
  }

  private void saveRefreshTokenInRedis(User user, String refreshToken) {
    redisTemplate.opsForValue()
        .set(REFRESH_TOKEN_PREFIX + user.getId(),
            refreshToken,
            jwtTokenProvider.getTokenExpiredIn(refreshToken) - new Date().getTime(),
            TimeUnit.MILLISECONDS);

  }
}
