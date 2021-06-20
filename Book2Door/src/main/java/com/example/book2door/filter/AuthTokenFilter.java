package com.example.book2door.filter;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import com.example.book2door.service.ClientServiceImpl;
import com.example.book2door.service.StoreServiceImpl;
import com.example.book2door.component.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger debugLogger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private StoreServiceImpl storeService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest,
                                    @NotNull HttpServletResponse httpServletResponse,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        debugLogger.info("Performing Filter Internal");
        try {
            String jwt = parseJwt(httpServletRequest);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getEmailFromJwt(jwt);
                UserDetails user = clientService.loadUserByUsername(email);
                if(user!=null){
                    var authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    debugLogger.info("{}", user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                else{
                    UserDetails store = storeService.loadUserByUsername(email);
                    var authentication = new UsernamePasswordAuthenticationToken(
                        store, null, store.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    debugLogger.info("{}", store.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                
                
            
            }
        } catch (Exception e) {
            debugLogger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

}
