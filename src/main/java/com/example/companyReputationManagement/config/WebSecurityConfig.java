package com.example.companyReputationManagement.config;

import com.example.companyReputationManagement.jwt.CustomJwtAuthenticationConverter;
import com.example.companyReputationManagement.jwt.security.CustomAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static com.example.companyReputationManagement.jwt.security.KeyLoader.loadPrivateKey;
import static com.example.companyReputationManagement.jwt.security.KeyLoader.loadPublicKey;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${app.secretKey}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint entryPoint) throws Exception {
        try {

            http.exceptionHandling(exceptionHandling ->
                            exceptionHandling
                                    .authenticationEntryPoint(entryPoint)
                    )
                    .authorizeHttpRequests

                            ((requests) -> requests
                                    .requestMatchers("/", "/home").permitAll()
                                    .requestMatchers("/auth/*").permitAll()
                                    .requestMatchers("/auth/login").permitAll()
                                    .requestMatchers("/user/edit").permitAll()
                                    .requestMatchers("/company/get_all").permitAll()
                                    .requestMatchers("/company/*").authenticated()
                                    .requestMatchers("/review/*").authenticated()
                                    .requestMatchers("/.well-known/**", "/oauth2/**").permitAll()
                                    .anyRequest().authenticated()

                            ).oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt
                                    .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
                            ).authenticationEntryPoint(entryPoint)
                    )
                    .logout(LogoutConfigurer::permitAll).csrf(AbstractHttpConfigurer::disable);
        } catch (JwtException e) {
            System.out.println(e.getMessage());
        }
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client") // Уникальный ID клиента
                .clientSecret("{noop}secret") // Простой секрет клиента. Использование {noop} указывает на отсутствие кодирования пароля
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://localhost:8080/login/oauth2/code/custom")
                .scope(OidcScopes.OPENID)
                .scope("read")
                .build();


        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        String publicKeyPath = "src/main/resources/rsa/public.pem";
        String privateKeyPath = "src/main/resources/rsa/private.pem";
        String privateKeyPem = Files.readString(Path.of(privateKeyPath));
        String publicKeyPem = Files.readString(Path.of(publicKeyPath));

        RSAPublicKey publicKey = loadPublicKey(publicKeyPem);
        RSAPrivateKey privateKey = loadPrivateKey(privateKeyPem);


        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(secretKey)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer("http://localhost:9000")
                .build();
    }
}