package com.pollypropilen.web.security;

import com.google.gson.Gson;
import com.pollypropilen.web.payload.misc.ObjectType;
import com.pollypropilen.web.payload.response.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e
    ) throws IOException {
        MessageResponse loginResponse = MessageResponse.ERROR("401 - Bad credentials", ObjectType.AUTH);
        String jsonLoginResponse = new Gson().toJson(loginResponse);
        httpServletResponse.setContentType(SecurityConstants.CONTENT_TYPE);
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.getWriter().println(jsonLoginResponse);
    }
}