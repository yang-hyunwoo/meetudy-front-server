package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.PhoneNumber;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PhoneNumberValidatorTest {

    private MessageSource messageSource;
    private PhoneNumberValidator validator;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        validator = new PhoneNumberValidator(messageSource);
    }

    @Test
    @DisplayName("전화번호 유효성 검사 - 성공")
    void valid_phoneNumber_should_pass() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        boolean result = validator.isValid("01012345678", context);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("전화번호 유효성 검사 - 010으로 시작하지 않을 때")
    void invalid_phoneNumber_should_fail_start() {
        ConstraintValidatorContext context = mockContext();
        when(messageSource.getMessage(eq("phone.start"), any(), eq(Locale.getDefault())))
                .thenReturn("전화번호는 010으로 시작해야 합니다.");

        boolean result = validator.isValid("01112345678", context);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("전화번호 유효성 검사 - 숫자가 아닌 문자가 포함될 때")
    void invalid_phoneNumber_should_fail_non_numeric() {
        ConstraintValidatorContext context = mockContext();
        when(messageSource.getMessage(eq("phone.numeric"), any(), eq(Locale.getDefault())))
                .thenReturn("전화번호는 숫자만 입력해야 합니다.");

        boolean result = validator.isValid("0101234abcd", context);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("전화번호 유효성 검사 - 길이가 11자리가 아닐 때")
    void invalid_phoneNumber_should_fail_length() {
        ConstraintValidatorContext context = mockContext();
        when(messageSource.getMessage(eq("phone.length"), any(), eq(Locale.getDefault())))
                .thenReturn("전화번호는 11자리여야 합니다.");

        boolean result = validator.isValid("0101234", context);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("전화번호 유효성 검사 - 공백 허용")
    void blank_phoneNumber_should_pass() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        assertThat(validator.isValid("", context)).isTrue();
        assertThat(validator.isValid(" ", context)).isTrue();
        assertThat(validator.isValid(null, context)).isTrue();
    }

    private ConstraintValidatorContext mockContext() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
        return context;
    }
}
