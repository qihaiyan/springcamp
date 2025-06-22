package cn.springcamp.spring.advanced.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    public void init() {
        this.setUserDetailsService(customUserDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        super.additionalAuthenticationChecks(userDetails, authentication);
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username = userDetails.getUsername();

        // 自定义认证校验逻辑
        if (username.equals("need approval")) {
            log.info("invalid request is: {}", req);
            throw new AuthenticationServiceException("Your account is pending approval for access");
        }
    }
}
