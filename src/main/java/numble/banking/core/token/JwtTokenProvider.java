package numble.banking.core.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BusinessException;
import numble.banking.core.common.error.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.accesstoken-expiration}")
  private Long accessTokenExpirationTime;

  @Value("${jwt.refreshtoken-expiration}")
  private Long refreshTokenExpirationTime;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String generateAccessToken(TokenData tokenData) {
    return generateToken(tokenData.getUserId(),
        tokenData.getEmail(),
        tokenData.getRole(),
        accessTokenExpirationTime);
  }

  public String generateRefreshToken(TokenData tokenData) {
    return generateToken(tokenData.getUserId(),
        tokenData.getEmail(),
        tokenData.getRole(),
        refreshTokenExpirationTime);
  }

  public String getUserEmail(String token) {
    if(!isExistToken(token)) throw new BusinessException(ErrorCode.TOKEN_NOT_EXISTS);

    Claims claims = getClaims(token);
    return claims.get("email", String.class);
  }

  public Long getUserId(String token) {
    if(!isExistToken(token)) throw new BusinessException(ErrorCode.TOKEN_NOT_EXISTS);
    Claims claims = getClaims(token);
    return claims.get("userId", Long.class);
  }

  public String getUserRole(String token) {
    if(!isExistToken(token)) throw new BusinessException(ErrorCode.TOKEN_NOT_EXISTS);
    Claims claims = getClaims(token);
    return claims.get("role", String.class);
  }

  public void validateToken(String token) {
    if(!isExistToken(token)) throw new BusinessException(ErrorCode.TOKEN_NOT_EXISTS);

    try {
      if(!isTokenExpired(token)) throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED);
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("Invalid JWT Token", e);
      throw new UnAuthorizedException(ErrorCode.INVALID_VERIFICATION_TOKEN);
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT Token", e);
      throw new UnAuthorizedException(ErrorCode.EXPIRED_VERIFICATION_TOKEN);
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT Token", e);
      throw new UnAuthorizedException(ErrorCode.CERTIFICATION_TYPE_NOT_MATCH);
    } catch (IllegalArgumentException e) {
      log.info("JWT claims string is empty.", e);
      throw e;
    }
  }

  public Long getTokenExpiredIn(String token) {
    Claims claims = getClaims(token);
    return claims.getExpiration().getTime();
  }

  private String generateToken(Long id, String email, String role, Long expireTime) {
    Claims claims = Jwts.claims();
    claims.put("userId", id);
    claims.put("email", email);
    claims.put("role", role);
    Date now = new Date(System.currentTimeMillis());

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expireTime))
        .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS256)
        .compact();
  }

  private Key getSigningKey(String secretKey) {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey(secretKey))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Boolean isTokenExpired(String token) {
    Date expiration = getClaims(token).getExpiration();
    return expiration.before(new Date());
  }

  private Boolean isExistToken(String token) {
    return token != null && token.length() != 0;
  }

}
