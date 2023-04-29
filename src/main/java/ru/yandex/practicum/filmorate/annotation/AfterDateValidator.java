package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

//import static ru.yandex.practicum.filmorate.model.Film.formatter;

public class AfterDateValidator implements ConstraintValidator<After, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(After constraintAnnotation) {
            date = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(date);
    }
}
