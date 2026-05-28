package co.edu.uniquindio.gestionacademica.config;

import co.edu.uniquindio.gestionacademica.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                //Desactivar CSRF porque usamos tokens JWT, no sesiones
                .csrf(csrf -> csrf.disable())
                //No usar sesiones, cada petición se autentica con el token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        //Estos endpoints son públicos, no necesitan token
                        .requestMatchers("/auth/**").permitAll()
                        //Solo ADMINISTRATIVO puede gestionar usuarios
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRATIVO")
                        //DOCENTE y ADMINISTRATIVO pueden gestionar solicitudes
                        .requestMatchers(HttpMethod.PATCH, "/solicitudes/*/clasificar")
                        .hasAnyRole("DOCENTE", "ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.PATCH, "/solicitudes/*/asignar")
                        .hasAnyRole("DOCENTE", "ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.PATCH, "/solicitudes/*/atender")
                        .hasAnyRole("DOCENTE", "ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.PATCH, "/solicitudes/*/resolver")
                        .hasAnyRole("DOCENTE", "ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.PATCH, "/solicitudes/*/cerrar")
                        .hasAnyRole("DOCENTE", "ADMINISTRATIVO")
                        //Tanto un ESTUDIANTE, como DOCENTE y ADMINISTRATIVO pueden pedir resumen con IA
                        .requestMatchers(HttpMethod.GET, "/ia/resumir/**").authenticated()
                        //Cualquier usuario autenticado puede pedir sugerencia de clasificación
                        .requestMatchers(HttpMethod.POST, "/ia/sugerir-clasificacion").authenticated()
                        //Cualquier usuario autenticado puede crear y consultar solicitudes
                        .anyRequest().authenticated())

                //Agrega el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //Bean para encriptar contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Bean que Spring Security usa para autenticar usuarios
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}