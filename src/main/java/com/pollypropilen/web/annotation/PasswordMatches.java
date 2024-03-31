package com.pollypropilen.web.annotation;

import com.pollypropilen.web.validation.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "Password and Password Confirm do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}