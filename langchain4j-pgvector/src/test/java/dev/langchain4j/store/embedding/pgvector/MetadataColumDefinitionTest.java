package dev.langchain4j.store.embedding.pgvector;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * These tests mutate the JVM-wide default locale, so the whole class runs {@link Isolated}.
 * {@code tr} and {@code az} lowercase {@code 'I'} to the dotless {@code 'ı'} (U+0131), which
 * must not leak into the parsed SQL type (e.g. {@code BIGINT} becoming {@code "bıgınt"} and
 * producing an invalid column definition).
 */
@Isolated
class MetadataColumDefinitionTest {

    private Locale defaultLocale;

    @BeforeEach
    void saveDefaultLocale() {
        defaultLocale = Locale.getDefault();
    }

    @AfterEach
    void restoreDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @ParameterizedTest
    @CsvSource({"tr-TR", "az"})
    void should_parse_type_independently_of_default_locale(String languageTag) {
        Locale.setDefault(Locale.forLanguageTag(languageTag));

        MetadataColumDefinition definition = MetadataColumDefinition.from("score BIGINT NOT NULL");

        assertThat(definition.getType()).isEqualTo("bigint");
    }
}
