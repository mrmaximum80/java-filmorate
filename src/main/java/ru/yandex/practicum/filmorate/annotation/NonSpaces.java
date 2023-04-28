package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpaceValidator.class)
public @interface NonSpaces {
    String message() default
            "Login не должен содержать пробелов";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
