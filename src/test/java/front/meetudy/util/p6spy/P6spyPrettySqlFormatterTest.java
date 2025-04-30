package front.meetudy.util.p6spy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class P6spyPrettySqlFormatterTest {

    P6spyPrettySqlFormatter formatter = new P6spyPrettySqlFormatter();

    @Test
    @DisplayName("SELECT SQL은 Hibernate BASIC 포맷으로 처리된다.")
    void shouldFormatDmlSqlWithBasicFormatter() {
        // given
        String sql = "select * from users where id = 1";

        // when
        String result = formatter.formatMessage(1, "now", 123, "statement", "", sql, "");

        // then
        assertThat(result).contains("HeFormatSql(P6Spy sql,Hibernate format):");
        assertThat(result).contains("select");
        assertThat(result).contains("OperationTime : 123ms");
    }

    @Test
    @DisplayName("DDL 문(create)은 Hibernate DDL 포맷으로 처리된다")
    void shouldFormatDdlSqlWithDdlFormatter() {
        // given
        String sql = "create table test (id bigint);";

        // when
        String result = formatter.formatMessage(2, "now", 50, "statement", "", sql, "");

        // then
        assertThat(result).contains("create table");
        assertThat(result).contains("HeFormatSql(P6Spy sql,Hibernate format):");
    }

    @Test
    @DisplayName("빈 SQL 입력 시 null 또는 빈 문자열 반환")
    void shouldReturnEmptyIfSqlIsNullOrBlank() {
        assertThat(formatter.formatMessage(1, "now", 0, "statement", "", "", "")).doesNotContain("HeFormatSql");
        assertThat(formatter.formatMessage(1, "now", 0, "statement", "", "   ", "")).doesNotContain("HeFormatSql");
    }

    @Test
    @DisplayName("STATEMENT 외 카테고리는 포맷하지 않는다")
    void shouldNotFormatIfNotStatementCategory() {
        String rawSql = "SELECT * FROM users";
        String result = formatter.formatMessage(1, "now", 10, "resultset", "", rawSql, "");
        assertThat(result).doesNotContain("HeFormatSql");
    }

}