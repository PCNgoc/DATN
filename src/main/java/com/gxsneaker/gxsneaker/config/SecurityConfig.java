package com.gxsneaker.gxsneaker.config;

import com.gxsneaker.gxsneaker.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] ADMIN_AUTHORITIES = {
            "ADMIN",
            "ROLE_ADMIN"
    };

    private static final String[] ADMIN_STAFF_AUTHORITIES = {
            "ADMIN",
            "ROLE_ADMIN",
            "STAFF",
            "ROLE_STAFF",
            "NHAN_VIEN",
            "ROLE_NHAN_VIEN"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/payos/**").permitAll()

                        // USER PUBLIC ORDER
                        .requestMatchers(HttpMethod.POST, "/api/hoa-don/dat-hang").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don/khach-hang/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/hoa-don/ap-dung-ma-giam-gia").permitAll()

                        // ADMIN ONLY
                        .requestMatchers("/api/nhan-vien/**").hasAnyAuthority(ADMIN_AUTHORITIES)
                        .requestMatchers("/api/hoa-don/thong-ke/**").hasAnyAuthority(ADMIN_AUTHORITIES)

                        // ADMIN + STAFF
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don").hasAnyAuthority(ADMIN_STAFF_AUTHORITIES)
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don/search").hasAnyAuthority(ADMIN_STAFF_AUTHORITIES)
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don/page").hasAnyAuthority(ADMIN_STAFF_AUTHORITIES)
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don/filter-date").hasAnyAuthority(ADMIN_STAFF_AUTHORITIES)
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don/*/history").hasAnyAuthority(ADMIN_STAFF_AUTHORITIES)
                        .requestMatchers(HttpMethod.PUT, "/api/hoa-don/*/status").hasAnyAuthority(ADMIN_STAFF_AUTHORITIES)

                        .requestMatchers(HttpMethod.DELETE, "/api/hoa-don/*").hasAnyAuthority(ADMIN_AUTHORITIES)

                        // CÒN LẠI TẠM MỞ
                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174"
        ));

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}