package it.epicode.FoodManager.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//QUESTA ANNOTAZIONE SERVE A COMUNICARE A SPRING CHE QUESTA  CLASSE Ã¨ UTILIZZATA PER CONFIGURARE LA SECURITY
@EnableWebSecurity()
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

    @Bean
    PasswordEncoder stdPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    AuthTokenFilter authenticationJwtToken() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Utilizza la configurazione CORS
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/users/login").permitAll()
                              .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                                .requestMatchers(HttpMethod.GET,"/categories").permitAll()
                                .requestMatchers(HttpMethod.POST,"/categories").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/api/products/**").hasAuthority("ADMIN")

//                                .requestMatchers(HttpMethod.GET, "/**").authenticated() //TUTTE GLI ENDPOINTS DI TIPO GET SONO RICHIAMABILI SOLO SE L'UTENTE E AUTENTICATO
//                                .requestMatchers(HttpMethod.POST, "/**").hasAuthority("ADMIN") //TUTTE LE POST POSSONO ESSERE FATTE SOLO DALL'ADMIN
//                                .requestMatchers(HttpMethod.PATCH, "/users/{id}").authenticated() //SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
//                                .requestMatchers(HttpMethod.PUT, "/**").hasAuthority("ADMIN") //TUTTE LE PUT POSSONO ESSERE FATTE SOLO DALL'ADMIN
//                                .requestMatchers(HttpMethod.DELETE, "/**").hasAuthority("ADMIN") //TUTTE LE DELETE POSSONO ESSERE FATTE SOLO DALL'ADMIN
//                                .requestMatchers("/**").authenticated() //TUTTO CIO CHE PUO ESSERE SFUGGITO RICHIEDE L'AUTENTICAZIONE (SERVE A GESTIRE EVENTUALI DIMENTICANZE)
                                //.requestMatchers("/**").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/roles").permitAll()

                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //COMUNICA ALLA FILTERCHAIN QUALE FILTRO UTILIZZARE, SENZA QUESTA RIGA DI CODICE IL FILTRO NON VIENE RICHIAMATO
                .addFilterBefore(authenticationJwtToken(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
