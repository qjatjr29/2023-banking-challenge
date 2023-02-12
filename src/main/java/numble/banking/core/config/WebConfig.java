package numble.banking.core.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import numble.banking.core.common.presentation.AuthenticationInterceptor;
import numble.banking.core.common.presentation.LoginUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final AuthenticationInterceptor authenticationInterceptor;
  private final LoginUserArgumentResolver loginUserArgumentResolver;

  // todo : path Pattern 변경
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authenticationInterceptor)
        .order(1)
        .addPathPatterns("/**");
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginUserArgumentResolver);
  }

}
