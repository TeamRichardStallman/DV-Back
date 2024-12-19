package org.richardstallman.dvback.global.config.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.global.jwt.JwtAuthorizationFilter;
import org.richardstallman.dvback.global.oauth.handler.OAuth2FailureHandler;
import org.richardstallman.dvback.global.oauth.handler.OAuth2SuccessHandler;
import org.richardstallman.dvback.global.oauth.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  @Value("${cors.allowed-origins.list}")
  private List<String> allowedOrigins;

  @Value("#{T(java.util.Arrays).asList('${security.excluded-paths}'.split(','))}")
  private List<String> excludedPaths;

  private final JwtAuthorizationFilter jwtAuthorizationFilter;
  private final CustomOAuth2UserService principalOauth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(
        List.of("Content-Type", "Authorization", "Accept", "Origin", "X-Requested-With"));
    configuration.setExposedHeaders(List.of("Authorization", "Location", "X-Custom-Header"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth -> {
              excludedPaths.forEach(path -> auth.requestMatchers(path).permitAll());
              auth.anyRequest().authenticated();
            })
        .exceptionHandling(ex -> ex.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(
            oauth2 ->
                oauth2
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
                    .userInfoEndpoint(userInfo -> userInfo.userService(principalOauth2UserService)))
        .build();
  }
}
