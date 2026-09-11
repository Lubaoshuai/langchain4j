package dev.langchain4j.model.googleai;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.googleai.GeminiContent.GeminiPart.GeminiCodeExecutionResult.GeminiOutcome;
import dev.langchain4j.model.googleai.GeminiContent.GeminiPart.GeminiExecutableCode.GeminiLanguage;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * These tests mutate the JVM-wide default locale, so the whole class runs {@link Isolated}.
 * {@code tr} and {@code az} lowercase {@code 'I'} to the dotless {@code 'ı'} (U+0131), which
 * must not leak into the values sent to the Gemini API (e.g. {@code INTEGER} becoming
 * {@code "ınteger"}).
 */
@Isolated
class GeminiEnumsLocaleTest {

    private Locale defaultLocale;

    @BeforeEach
    void setTurkishLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
    }

    @AfterEach
    void restoreDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @ParameterizedTest
    @CsvSource({
        "STRING, string",
        "NUMBER, number",
        "INTEGER, integer",
        "BOOLEAN, boolean",
        "ARRAY, array",
        "OBJECT, object",
        "NULL, null"
    })
    void should_map_type_independently_of_default_locale(GeminiType type, String expected) {
        assertThat(type.toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"USER, user", "MODEL, model"})
    void should_map_role_independently_of_default_locale(GeminiRole role, String expected) {
        assertThat(role.toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"PYTHON, python", "LANGUAGE_UNSPECIFIED, language_unspecified"})
    void should_map_executable_language_independently_of_default_locale(
            GeminiLanguage language, String expected) {
        assertThat(language.toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "OUTCOME_UNSPECIFIED, outcome_unspecified",
        "OUTCOME_OK, outcome_ok",
        "OUTCOME_FAILED, outcome_failed",
        "OUTCOME_DEADLINE_EXCEEDED, outcome_deadline_exceeded"
    })
    void should_map_outcome_independently_of_default_locale(GeminiOutcome outcome, String expected) {
        assertThat(outcome.toString()).isEqualTo(expected);
    }
}
