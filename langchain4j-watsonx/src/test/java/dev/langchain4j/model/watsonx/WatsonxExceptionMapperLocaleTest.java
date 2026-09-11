package dev.langchain4j.model.watsonx;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.ibm.watsonx.ai.core.exception.WatsonxException;
import com.ibm.watsonx.ai.core.exception.model.WatsonxError;
import com.ibm.watsonx.ai.core.exception.model.WatsonxError.Code;
import dev.langchain4j.exception.InvalidRequestException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * This test mutates the JVM-wide default locale, so the whole class runs {@link Isolated}.
 * {@code tr} and {@code az} uppercase {@code 'i'} to the dotted {@code 'İ'} (U+0130), which must
 * not prevent the watsonx error code from being recognized (e.g. {@code "invalid_input_argument"}
 * staying {@code "INVALID_INPUT_ARGUMENT"} instead of becoming {@code "İNVALİD_INPUT_ARGUMENT"}).
 */
@Isolated
class WatsonxExceptionMapperLocaleTest {

    private static final WatsonxExceptionMapper mapper = WatsonxExceptionMapper.INSTANCE;

    private Locale defaultLocale;

    @BeforeEach
    void saveDefaultLocale() {
        defaultLocale = Locale.getDefault();
    }

    @AfterEach
    void restoreDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    void should_map_error_code_independently_of_default_locale() {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        var details = new WatsonxError(
                400,
                "96d1304b909f98de10f1199e92d9b873",
                List.of(new WatsonxError.Error(Code.INVALID_INPUT_ARGUMENT.value(), "invalid input",
                        "https://cloud.ibm.com/apidocs/watsonx-ai")));
        var ex = mapper.mapException(new WatsonxException(
                Code.INVALID_INPUT_ARGUMENT.value(), 400, details));

        assertInstanceOf(InvalidRequestException.class, ex);
    }
}
