package com.pollypropilen.web.payload.request;

import com.pollypropilen.web.annotation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@PasswordMatches
public class SignupRequest {

    @NotEmpty(message = "Please enter your username")
    @NotBlank
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 4, message = "Password must be 4 symbols or more")
    private String password;

    private String confirmPassword;
}