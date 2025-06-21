package com.abhishek.authify.filter;

import com.abhishek.authify.service.AppUserDetailService;
import com.abhishek.authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final AppUserDetailService appUserDetailService;
    private final JwtUtil jwtUtil;
    private static final List<String> URL = List.of("/login","/register","/logout","/reset-password","/send-reset-otp");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getServletPath();
        if(URL.contains(url)){
            filterChain.doFilter(request,response);
            return;
        }
        String email=null;
        String jwt=null;
        String AuthorizationHeader = request.getHeader("Authorization");
        if(AuthorizationHeader!=null && AuthorizationHeader.startsWith("Bearer")){
            jwt= AuthorizationHeader.substring(7);

        }
        if(jwt==null){
            Cookie [] cookies = request.getCookies();
            for(Cookie cookie : cookies){
                if("jwt".equals(cookie.getName())){
                    jwt=cookie.getValue();
                    break;
                }
            }
        }

        if(jwt!=null){
            email=jwtUtil.extractEmail(jwt);
            if(email!=null && SecurityContextHolder.getContext().
            getAuthentication()==null){
                UserDetails userDetails = appUserDetailService.loadUserByUsername(email);
                if(jwtUtil.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }

            }
        }


        filterChain.doFilter(request,response);
    }
}
