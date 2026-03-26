package com.example.smartinventorymanagementbackend.config;

import com.example.smartinventorymanagementbackend.repository.UserAccountRepository;
import com.example.smartinventorymanagementbackend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository.findByEmail(username.toLowerCase())
                .map(user -> {
                    UserDetails details = User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getRole().name())
                            .build();
                    return details;
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/access/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/access/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/access/staff/**").hasAnyRole("STAFF", "MANAGER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
