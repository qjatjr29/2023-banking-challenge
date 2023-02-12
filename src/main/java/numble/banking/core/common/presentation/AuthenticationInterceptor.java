package numble.banking.core.common.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.UnAuthorizedException;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.user.command.domain.Role;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  private static final String HEADER_AUTHORIZATION = "Authorization";

  /**
   * Auth 가 null 이라면 로그인(권한)없이 접근 가능하다.
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    Auth auth = getAuth(handler, Auth.class);
    if(auth == null) return true;

    String token = extractToken(request);
    if(StringUtils.isEmpty(token)) throw new UnAuthorizedException(ErrorCode.TOKEN_NOT_EXISTS);

    jwtTokenProvider.validateToken(token);

    String userRole = jwtTokenProvider.getUserRole(token);

    // 토큰에 들어온 유저의 권한이 해당 API에 요구되는 권한에 속하는지 확인.
    if (Arrays.asList(auth.role()).contains(Role.valueOf(userRole))) return true;

    throw new UnAuthorizedException(ErrorCode.CERTIFICATION_TYPE_NOT_MATCH);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader(HEADER_AUTHORIZATION);
    if(StringUtils.hasText(header)) return header;
    return Strings.EMPTY;
  }

  private Auth getAuth(Object handler, Class cls) {
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    return (Auth) handlerMethod.getMethodAnnotation(cls);
  }

}
