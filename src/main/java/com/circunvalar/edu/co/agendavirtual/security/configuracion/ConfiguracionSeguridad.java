package com.circunvalar.edu.co.agendavirtual.security.configuracion;

import com.circunvalar.edu.co.agendavirtual.security.servicios.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de autenticacion, autorizacion y login por formulario.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class ConfiguracionSeguridad {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Define reglas de acceso y flujo de login/logout.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/perform_login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/api/**",
                                "/ia/**",
                                "/api/ia/**"
                        ).permitAll()

                        .requestMatchers("/admin/**")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                "/dashboard/**",
                                "/eventos/**",
                                "/recordatorios/**",
                                "/tareas/**"
                        )
                        .hasAnyRole(
                                "USUARIO",
                                "ADMINISTRADOR"
                        )

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl("/perform_login")

                        .usernameParameter("username")

                        .passwordParameter("password")

                        .defaultSuccessUrl("/dashboard", true)

                        .failureUrl("/login?error=true")

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout=true")

                        .invalidateHttpSession(true)

                        .deleteCookies("JSESSIONID")

                        .permitAll()
                )

                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    /**
     * Encoder de contrasenas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);
    }

    /**
     * Expone el AuthenticationManager configurado por Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}