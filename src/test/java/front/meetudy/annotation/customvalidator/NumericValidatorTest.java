package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Numeric;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class NumericValidatorTest {

    private MessageSource messageSource;
    private NumericValidator validator;

    @BeforeEach
    void setup() {
        messageSource = mock(MessageSource.class);
        validator = new NumericValidator(messageSource);

        Numeric annotation = new Numeric() {
            @Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return Numeric.class; }
            @Override public String message() { return "{default.numeric}"; }
            @Override public String messageKey() { return "numeric.range"; }
            @Override public int min() { return 2; }
            @Override public int max() { return 5; }
            @Override public int mid() { return 4; }
            @Override public boolean numberEquals() { return false; }
            @Override public Class<?>[] groups() { return new Class[0]; }
            @Override public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
        };

        validator.initialize(annotation);
    }

    @Test
    @DisplayName("숫자 유효성 검사 - 성공")
    void valid_numeric() {
        //given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        //when
        boolean result = validator.isValid("1234", context);
        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("정규식 실패 - 숫자가 아닌 경우")
    void non_numeric_fail() {
        // given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        when(messageSource.getMessage(eq("default.numeric"), any(), eq(Locale.getDefault())))
                .thenReturn("숫자만 입력 가능합니다.");

        // when
        boolean result = validator.isValid("aaa", context);
        boolean result2 = validator.isValid("aaa444", context);

        // then
        assertThat(result).isFalse();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("자릿수 범위 실패")
    void range_fail() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(messageSource.getMessage(eq("numeric.range"), eq(new Object[]{2, 5}), eq(Locale.getDefault())))
                .thenReturn("숫자는 2자 이상 5자 이하로 입력해야 합니다.");

        boolean result = validator.isValid("123456", context);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("mid 자릿수 체크 실패")
    void mid_length_fail() {
        Numeric annotation = new Numeric() {
            @Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return Numeric.class; }
            @Override public String message() { return "{numeric.pattern}"; }
            @Override public String messageKey() { return "numeric.range"; }
            @Override public int min() { return 0; }
            @Override public int max() { return 0; }
            @Override public int mid() { return 4; }
            @Override public boolean numberEquals() { return true; }
            @Override public Class<?>[] groups() { return new Class[0]; }
            @Override public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
        };

        validator.initialize(annotation);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(messageSource.getMessage(eq("numeric.range"), eq(new Object[]{4}), eq(Locale.getDefault())))
                .thenReturn("숫자는 정확히 4자리여야 합니다.");

        boolean result = validator.isValid("123", context);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("mid 자릿수 체크 ")
    void mid_length_pass() {
        Numeric annotation = new Numeric() {
            @Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return Numeric.class; }
            @Override public String message() { return "{numeric.pattern}"; }
            @Override public String messageKey() { return "numeric.range"; }
            @Override public int min() { return 0; }
            @Override public int max() { return 0; }
            @Override public int mid() { return 4; }
            @Override public boolean numberEquals() { return true; }
            @Override public Class<?>[] groups() { return new Class[0]; }
            @Override public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
        };

        validator.initialize(annotation);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(messageSource.getMessage(eq("numeric.range"), eq(new Object[]{4}), eq(Locale.getDefault())))
                .thenReturn("숫자는 정확히 4자리여야 합니다.");

        boolean result = validator.isValid("1243", context);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName(" 공백 허용")
    void blank_numeric_should_pass() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        assertThat(validator.isValid("", context)).isTrue();
        assertThat(validator.isValid(" ", context)).isTrue();
        assertThat(validator.isValid(null, context)).isTrue();
    }
}
