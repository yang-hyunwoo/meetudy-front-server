package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.KoreanEnglish;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.lang.annotation.Annotation;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KoreanEnglishValidatorTest {

    @Mock
    MessageSource messageSource;

    KoreanEnglishValidator validator;

    @BeforeEach
    void setUp() {
        validator = new KoreanEnglishValidator(messageSource);
        KoreanEnglish annotation = new KoreanEnglish() {
            public int min() { return 2; }
            public int max() { return 50; }
            public String message() { return "{name.pattern}"; }
            public String messageKey() { return "name.range"; }
            public Class<? extends Annotation> annotationType() { return KoreanEnglish.class; }
            public Class<?>[] groups() { return new Class[0]; }
            public Class<? extends Payload>[] payload() { return new Class[0]; }
        };
        validator.initialize(annotation);
    }

    @Test
    @DisplayName("한글 통과")
    void korean_pass() {
        // given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // when
        boolean isValid = validator.isValid("홍길동", context);

        // then
        assertThat(isValid).isTrue();
        verifyNoInteractions(messageSource); //메시지 생성 로직 안 탔는지 검증
    }

    @Test
    @DisplayName("영어 통과")
    void eng_pass() {
        // given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // when
        boolean isValid = validator.isValid("gausadf", context);

        // then
        assertThat(isValid).isTrue();
        verifyNoInteractions(messageSource); //메시지 생성 로직 안 탔는지 검증
    }

    @Test
    @DisplayName("한글/영어 통과")
    void kor_eng_pass() {
        // given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // when
        boolean isValid = validator.isValid("홍길동asdf", context);

        // then
        assertThat(isValid).isTrue();
        verifyNoInteractions(messageSource); //메시지 생성 로직 안 탔는지 검증
    }

    @Test
    @DisplayName("한글/영어 외 문자 실패")
    void korean_eng_fail() {
        // given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        // 메시지 소스 mock
        when(messageSource.getMessage("name.pattern", null, Locale.getDefault()))
                .thenReturn("이름은 한글 또는 영문만 입력 가능합니다.");

        // when
        boolean result = validator.isValid("1234", context);
        boolean result2 = validator.isValid("ㄱㄴㅇ", context);

        // then
        assertThat(result).isFalse();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("한글/영어 길이 실패")
    void korean_eng_length_fail() {
        // given
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        // 메시지 소스 mock
        when(messageSource.getMessage(eq("name.range"), eq(new Object[]{2, 50}), eq(Locale.getDefault())))
                .thenReturn("이름은 한글 또는 영문으로 2자 이상 50자 이하로 입력해야 합니다.");

        // when
        boolean result = validator.isValid("둥", context);
        boolean result2 = validator.isValid("일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오일이삼사오", context);

        // then
        assertThat(result).isFalse();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("공백 허용")
    void blank_kr_en_should_pass() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        assertThat(validator.isValid("", context)).isTrue();
        assertThat(validator.isValid(" ", context)).isTrue();
        assertThat(validator.isValid(null, context)).isTrue();
    }
}
