package cn.springcamp.spring.advanced.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.function.Supplier;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;
import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        MyRequestAuthorizationManager myRequestAuthorizationManager =
                new MyRequestAuthorizationManager();
        http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers("/login", "/logout").permitAll()
                        .requestMatchers("/{param}").access(myRequestAuthorizationManager)
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login").defaultSuccessUrl("/").permitAll()
                )
                .exceptionHandling(customizer ->
                        customizer.authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/login")))
        ;
        return http.build();
    }

    @Autowired
    void configure(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(false); // Do not clear credentials after authentication, so we have access to passwords on success handlers
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider);
    }
}

final class MyRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final SecurityExpressionHandler<RequestAuthorizationContext> expressionHandler = new DefaultHttpSecurityExpressionHandler();

    public MyRequestAuthorizationManager() {
    }

    // 自定义授权校验逻辑
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        EvaluationContext ctx = this.expressionHandler.createEvaluationContext(authentication, context);
        String checkParam = Optional.ofNullable(ctx.lookupVariable("param")).map(String::valueOf).orElse(null);

        // the '/public' url doesn't need authentication
        if (checkParam != null && (checkParam.equals("public"))) {
            return new AuthorizationDecision(true);
        }
        return new AuthorizationDecision(!ObjectUtils.isEmpty(authentication.get().getCredentials()));
    }
}
