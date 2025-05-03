package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Password;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
        validator.initialize(null);
    }

    @Test
    @DisplayName("패스워드 유효성 검사 - 성공")
    void valid_password_should_pass() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        String validPassword = "Abcd1234!"; // 영문, 숫자, 특수문자 포함

        boolean result = validator.isValid(validPassword, context);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("패스워드 유효성 검사 - 실패 (형식 오류)")
    void invalid_password_should_fail() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "abcd1234"; // 특수문자 없음

        boolean result = validator.isValid(invalidPassword, context);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("공백 허용")
    void blank_password_should_pass() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        assertThat(validator.isValid("", context)).isTrue();
        assertThat(validator.isValid(" ", context)).isTrue();
        assertThat(validator.isValid(null, context)).isTrue();
    }
}
