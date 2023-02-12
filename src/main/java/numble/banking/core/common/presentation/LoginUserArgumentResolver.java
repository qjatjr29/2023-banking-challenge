package numble.banking.core.common.presentation;

import lombok.extern.slf4j.Slf4j;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BusinessException;
import numble.banking.core.token.JwtTokenProvider;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

  private static final String HEADER_AUTHORIZATION = "Authorization";

  private final JwtTokenProvider jwtTokenProvider;

  public LoginUserArgumentResolver(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // 메소드에 바인딩할 클래스를 명시
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoginUser.class);
  }

  // JWT 토큰에서 값을 가져와 어노테이션에 저장.
  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

    String token = webRequest.getHeader(HEADER_AUTHORIZATION);

    if(token == null || token.length() == 0) throw new BusinessException(ErrorCode.TOKEN_NOT_EXISTS);

    return jwtTokenProvider.getUserId(token);
  }

}
