package ph.com.lllc.config.security;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ph.com.lllc.config.properties.SecurityPropertiesConfig;
import ph.com.lllc.enums.UserRole;
import ph.com.lllc.service.security.client.ClientUserDetailsService;
import ph.com.lllc.service.security.staff.StaffUserDetailsService;

import java.io.IOException;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final StaffUserDetailsService staffUserDetailsService;
    private final ClientUserDetailsService clientUserDetailsService;
    private final SecurityPropertiesConfig securityPropertiesConfig;

    private static final String J_SESSION_ID= "JSESSIONID";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManager staffAuthenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(staffUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public AuthenticationManager cclientAuthenticationManager(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(clientUserDetailsService);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    @Order(0)
    public SecurityFilterChain publicChain(HttpSecurity http) {

        String[] publicPaths = Stream.of(
                securityPropertiesConfig.getSwaggerPath(),
                securityPropertiesConfig.getStaticPath(),
                securityPropertiesConfig.getInternalPath(),
                securityPropertiesConfig.getDefaultPath()
        ).flatMap(Stream::of).toArray(String[]::new);

        http
                .securityMatcher(publicPaths)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain clientChain(HttpSecurity http) {

        String[] clientPaths = Stream.of(securityPropertiesConfig.getClientPath()).toArray(String[]::new);

        http
                .securityMatcher(clientPaths)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(securityPropertiesConfig.getClientLogin())
                        .permitAll()
                        .requestMatchers(securityPropertiesConfig.getClientPath())
                        .hasAnyRole(UserRole.KID.name(), UserRole.PARENT.name())
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage(securityPropertiesConfig.getClientLogin())
                        .loginProcessingUrl(securityPropertiesConfig.getClientLogin())
                        .defaultSuccessUrl(securityPropertiesConfig.getClientDashboard(), true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(securityPropertiesConfig.getClientLogout())
                        .logoutSuccessUrl(securityPropertiesConfig.getClientLogin())
                        .invalidateHttpSession(true)
                        .deleteCookies(J_SESSION_ID)
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                handleSecurityException(request, response, accessDeniedException, HttpServletResponse.SC_FORBIDDEN))
                        .authenticationEntryPoint((request, response, authException) ->
                                handleSecurityException(request, response, authException, HttpServletResponse.SC_UNAUTHORIZED))
                );

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain staffChain(HttpSecurity http) {

        String[] staffPaths = Stream.of(securityPropertiesConfig.getStaffPath()).toArray(String[]::new);

        http
                .securityMatcher(staffPaths)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(securityPropertiesConfig.getStaffLogin())
                        .permitAll()
                        .requestMatchers(securityPropertiesConfig.getStaffPath())
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.CASE_MANAGER.name(),
                                UserRole.THERAPIST.name(), UserRole.ACCOUNTING.name(),
                                UserRole.EMPLOYEE.name())
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage(securityPropertiesConfig.getStaffLogin())
                        .loginProcessingUrl(securityPropertiesConfig.getStaffLogin())
                        .defaultSuccessUrl(securityPropertiesConfig.getStaffDashboard(), true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(securityPropertiesConfig.getStaffLogout())
                        .logoutSuccessUrl(securityPropertiesConfig.getStaffLogin())
                        .invalidateHttpSession(true)
                        .deleteCookies(J_SESSION_ID)
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                handleSecurityException(request, response, accessDeniedException, HttpServletResponse.SC_FORBIDDEN))
                        .authenticationEntryPoint((request, response, authException) ->
                                handleSecurityException(request, response, authException, HttpServletResponse.SC_UNAUTHORIZED))
                );

        return http.build();
    }

    /* CENTRALIZED SECURITY EXCEPTION HANDLER */
    private void handleSecurityException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Exception exception,
                                         int defaultStatus) throws IOException {

        /* Invalidate session if exists */
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        /* Remove JSESSIONID cookie */
        Cookie cookie = new Cookie(J_SESSION_ID, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        /* Determine HTTP status */
        int status;
        if (exception instanceof AccessDeniedException) {
            status = HttpServletResponse.SC_FORBIDDEN;
        } else if (exception instanceof AuthenticationException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        } else {
            status = defaultStatus;
        }

        /* Forward to centralized error page safely */
        request.setAttribute("jakarta.servlet.error.status_code", status);
        RequestDispatcher dispatcher = request.getRequestDispatcher(securityPropertiesConfig.getErrorPagePath());
        if (dispatcher != null) {
            try {
                dispatcher.forward(request, response);
            } catch (Exception e) {
                response.sendError(status, exception.getMessage());
            }
        } else {
            response.sendError(status, exception.getMessage());
        }
    }
}
