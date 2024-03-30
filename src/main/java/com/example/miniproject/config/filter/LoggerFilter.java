package com.example.miniproject.config.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggerFilter implements Filter {

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var request = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        var response = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
        log.info("URI : {}", request.getRequestURI());
        filterChain.doFilter(request, response);

        var headerNames = request.getHeaderNames();
        var requestHeaderValues = new StringBuilder();
        headerNames.asIterator().forEachRemaining(headerKey -> {
            var headerValue = request.getHeader(headerKey);
            requestHeaderValues.append("[").append(headerKey).append(" : ").append(headerValue).append("]");
        });

        var uri = request.getRequestURI();
        var method = request.getMethod();
        var requestBody = new String(request.getContentAsByteArray());

        if (uri.equalsIgnoreCase("/api/members/login") || uri.equalsIgnoreCase("/api/members/my-info") && method.equalsIgnoreCase("PATCH")) {
            Map<String, Object> jsonObject = objectMapper.readValue(requestBody, new TypeReference<>() {});
            Map<String, Object> result = new HashMap<>();
            jsonObject.forEach((key, value) -> {
                if (key.contains("password")) {
                    value = "***********";
                }
                result.put(key, value);
            });
            requestBody = objectMapper.writeValueAsString(result);
        }

        log.info("-> request uri : {}, method : {}, header : {}, body : {}", uri, method, requestHeaderValues, requestBody);

        var responseHeaderValues = new StringBuilder();
        response.getHeaderNames().forEach(headerKey -> {
            var headerValue = response.getHeader(headerKey);
            responseHeaderValues.append("[").append(headerKey).append(" : ").append(headerValue).append("]");
        });
        var responseBody = new String(response.getContentAsByteArray());

        log.info("<- response uri : {}, method : {}, header : {}, body : {}", uri, method, responseHeaderValues, responseBody);

        response.copyBodyToResponse();
    }

}
