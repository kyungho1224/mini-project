package com.example.miniproject.config;

import com.example.miniproject.config.filter.JwtTokenFilter;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
          .httpBasic(AbstractHttpConfigurer::disable)
          .csrf(AbstractHttpConfigurer::disable)
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(request -> {
              request
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/api/members/join", "/api/members/login", "/api/members/verify", "/api/members/upload-profile").permitAll()
                .anyRequest().authenticated();
          })
          .addFilterBefore(new JwtTokenFilter(jwtTokenUtil, memberRepository), UsernamePasswordAuthenticationFilter.class)
          .formLogin(Customizer.withDefaults())
          .logout(Customizer.withDefaults());

        return httpSecurity.build();
    }

}
