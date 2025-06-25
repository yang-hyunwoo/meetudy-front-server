package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.EnumValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * enum class 검증 validator
 */
public class EnumValidator implements ConstraintValidator<EnumValidation, String> {
    private Set<String> acceptedValues;

    @Override
    public void initialize(EnumValidation annotation) {
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && acceptedValues.contains(value) && !value.isBlank();
    }
}
