package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Email;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EmailValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static class TestDto {
        @Email
        private String email;

        public TestDto(String email) {
            this.email = email;
        }
    }

    @Test
    @DisplayName("이메일 어노테이션 검증 성공")
    void valid_email_should_pass() {
        // given
        var dto = new TestDto("test@example.com");
        // when
        var result = validator.validate(dto);
        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이메일 어노테이션 검증 실패")
    void invalid_email_should_fail() {
        //given
        var dto = new TestDto("test");
        //when
        var result = validator.validate(dto);
        //then
        assertThat(result).isNotEmpty();

        ConstraintViolation<TestDto> violation  = result.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("공백 허용")
    void blank_email_should_pass() {
        EmailValidator emailValidator = new EmailValidator(); // 직접 테스트할 validator 생성
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        assertThat(emailValidator.isValid("", context)).isTrue();
        assertThat(emailValidator.isValid(" ", context)).isTrue();
        assertThat(emailValidator.isValid(null, context)).isTrue();
    }
}
