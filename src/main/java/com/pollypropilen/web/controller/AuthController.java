package com.pollypropilen.web.controller;

import com.pollypropilen.web.dto.TokenDTO;
import com.pollypropilen.web.entity.User;
import com.pollypropilen.web.exception.UserExistException;
import com.pollypropilen.web.payload.misc.ObjectFormat;
import com.pollypropilen.web.payload.misc.ObjectType;
import com.pollypropilen.web.payload.request.LoginRequest;
import com.pollypropilen.web.payload.request.SignupRequest;
import com.pollypropilen.web.payload.response.MessageResponse;
import com.pollypropilen.web.security.JWTTokenProvider;
import com.pollypropilen.web.services.CustomUserDetailsService;
import com.pollypropilen.web.services.UserService;
import com.pollypropilen.web.validation.ResponseErrorValidation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
public class AuthController {
    public static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorValidation responseErrorValidation;
    private final UserService userService;
    private final CustomUserDetailsService customUserService;

    @Autowired
    public AuthController(JWTTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager,
                          ResponseErrorValidation responseErrorValidation,
                          UserService userService,
                          CustomUserDetailsService customUserService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.responseErrorValidation = responseErrorValidation;
        this.userService = userService;
        this.customUserService = customUserService;
    }

//    @PostMapping("/generateToken")
//    public ResponseEntity<MessageResponse> authenticateAndGetToken(HttpServletRequest request) {
//        LOG.debug("[AUTH] Generate token");
//        TokenDTO token = jwtTokenProvider.generateToken(request);
//        return ResponseEntity.ok(MessageResponse.DATA("New token", ObjectType.TOKEN, ObjectFormat.SINGLE, token));
//    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult) {
        LOG.debug("[AUTH] User Singing Up");
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return ResponseEntity.ok(
                    MessageResponse.ERROR("Error during user registration: " + Objects.requireNonNull(errors.getBody()), ObjectType.USER));
        }
        try {
            userService.createUser(signupRequest);
            return ResponseEntity.ok(MessageResponse.OK("User registered successfully", ObjectType.USER));
        } catch (UserExistException ex) {
            return ResponseEntity.ok(MessageResponse.ERROR("Error during user registration: " + ex.getMessage(), ObjectType.USER));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<MessageResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            BindingResult bindingResult) {
        LOG.debug("[AUTH] User Singing");
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return ResponseEntity.ok(MessageResponse
                    .ERROR(
                            "Error during user signing: " + Objects.requireNonNull(errors.getBody()),
                            ObjectType.USER
                    )
            );
        }
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenDTO token = jwtTokenProvider.generateUserToken(authentication, request);
        User user = (User) authentication.getPrincipal();
        customUserService.addNewUserToken(user, token.getToken());
        return ResponseEntity.ok(MessageResponse.DATA("User logged successfully", ObjectType.TOKEN, ObjectFormat.SINGLE, token));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(Principal principal) {
        LOG.debug("[AUTH] User Logout");
        if (principal == null) {
            return ResponseEntity.ok(MessageResponse.ERROR("Error during user logout: User not logged", ObjectType.USER));
        }
        User user = userService.getCurrentUser(principal);
        customUserService.lockAllUserTokens(user);
        return ResponseEntity.ok(MessageResponse.OK("User logout successfully", ObjectType.USER));
    }

}