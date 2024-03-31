package com.pollypropilen.web.annotation;

import com.pollypropilen.web.validation.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
    String message() default "Invalid EMail format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}