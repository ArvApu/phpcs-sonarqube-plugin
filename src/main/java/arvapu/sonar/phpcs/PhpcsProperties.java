package arvapu.sonar.phpcs;

import org.sonar.api.config.PropertyDefinition;

import java.util.Collections;
import java.util.List;

public class PhpcsProperties {
    public static final String REPORT_PATH_KEY = "sonar.php.phpcs.reportPath";
    public static final String REPORT_PATH_DEFAULT = "output/phpcs.json";

    private PhpcsProperties() {
        // Utility class
    }

    public static List<PropertyDefinition> getProperties() {
        return Collections.singletonList(
            PropertyDefinition.builder(REPORT_PATH_KEY)
                .name("PHPCS Report Path")
                .description("Path to the PHPCS JSON report file")
                .defaultValue(REPORT_PATH_DEFAULT)
                .build()
        );
    }
}
