package com.example.demo.security.filter;

import com.example.demo.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION); // ra value của authorization : bearer <token>
            if(header == null) {
                // nếu không có header thì cho phép request đi tiếp
                filterChain.doFilter(request, response);
                return;
            }
            String token = header.substring(7); // lấy token từ header
            if (!jwtUtils.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            String email = jwtUtils.getEmailFromToken(token); // lấy email từ token
            List<SimpleGrantedAuthority> authorities = jwtUtils.getRoleFromToken(token).stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .toList();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // tạo đối tượng authenticationToken từ userDetails và authorities
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            // dựa vào authenticationToken để tạo ra một đối tượng SecurityContext để lưu trữ thông tin xác thực
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response); // cho phép request đi tiếp

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
