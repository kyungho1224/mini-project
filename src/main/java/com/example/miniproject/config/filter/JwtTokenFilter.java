package com.example.miniproject.config.filter;

import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.dto.MemberDetails;
import com.example.miniproject.domain.member.dto.TokenDTO;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.util.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token;
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            token = header.split(" ")[1].trim();
            try {
                String email = jwtTokenUtil.validationTokenWithThrow(token);
                MemberDetails member = loadMemberByEmail(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                  member, null, member.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                if (e instanceof ExpiredJwtException) {
                    regeneratedToken(response, token);
                } else {
                    throw new ApiException(ApiErrorCode.TOKEN_ERROR.getDescription());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    //    @Transactional
    public void regeneratedToken(HttpServletResponse response, String token) {
        String accessToken = "";
        try {
            String email = jwtTokenUtil.decodeJwtPayloadEmail(token);
            Member member = memberRepository.findByEmailAndStatus(email, MemberStatus.CERTIFICATED)
              .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));

            String refreshToken = member.getRefreshToken();
            if (jwtTokenUtil.isExpired(refreshToken)) {
                TokenDTO tokenDTO = jwtTokenUtil.regeneratedToken(token);
                member.updateRefreshToken(tokenDTO.getRefreshToken());
                accessToken = tokenDTO.getAccessToken();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    private MemberDetails loadMemberByEmail(String email) {
        return memberRepository.findByEmailAndStatus(email, MemberStatus.CERTIFICATED)
          .map(MemberDetails::of)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));
    }

}
