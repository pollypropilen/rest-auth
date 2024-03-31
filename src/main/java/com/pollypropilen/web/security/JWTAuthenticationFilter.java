package com.pollypropilen.web.security;

import com.pollypropilen.web.entity.User;
import com.pollypropilen.web.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        int res = HttpServletResponse.SC_OK;
        try {
            String jwt = getJWTFromRequest(httpServletRequest);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                //check user IP
                String ip = jwtTokenProvider.getUserIPFromToken(jwt);
                if (!ip.equals(getClientIp(httpServletRequest))) {
                    res = HttpServletResponse.SC_UNAUTHORIZED;
                }
                //check user agent name
                if (res == HttpServletResponse.SC_OK) {
                    String ua = jwtTokenProvider.getUserUAFromToken(jwt);
                    if (!ua.equals(getUserAgent(httpServletRequest))) {
                        res = HttpServletResponse.SC_UNAUTHORIZED;
                    }
                }
                //check token issuing date-time
                if (res == HttpServletResponse.SC_OK) {
                    Long issuingTime = jwtTokenProvider.getTimeFromToken(jwt);
                    if ((System.currentTimeMillis() - issuingTime) > SecurityConstants.EXPIRATION_TIME) {
                        res = HttpServletResponse.SC_UNAUTHORIZED;
                    }
                }
                //check user record
                if (res == HttpServletResponse.SC_OK) {
                    User userDetails = customUserDetailsService.loadUserById(jwtTokenProvider.getUserIdFromToken(jwt));
                    if (userDetails == null || !customUserDetailsService.checkUserToken(userDetails, jwt)) {
                        res = HttpServletResponse.SC_UNAUTHORIZED;
                    }

                    if (res == HttpServletResponse.SC_OK) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, Collections.emptyList()
                        );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception ex) {
            res = HttpServletResponse.SC_UNAUTHORIZED;
            LOG.error("Could not set user authentication");
        }
        //continue chain filter if no errors
        if (res == HttpServletResponse.SC_OK) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            httpServletResponse.setStatus(res);
            //httpServletResponse.sendError(res, "{\"status\":\"UNAUTHORIZED\"}");
        }
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(SecurityConstants.HEADER_STRING);
        if (StringUtils.hasText(bearToken) && bearToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return bearToken.split(" ")[1];
        }
        return null;
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    public static String getUserAgent(HttpServletRequest request) {
        String ua = "";
        if (request != null) {
            ua = request.getHeader("User-Agent");
        }
        return ua;
    }

}