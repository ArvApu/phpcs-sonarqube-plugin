package arvapu.sonar.phpcs.extension;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

public class PhpcsProperties {
    public static final String REPORT_PATH_KEY = "sonar.php.phpcs.reportPath";
    public static final String REPORT_PATH_DEFAULT = "output/phpcs.json";

    private PhpcsProperties() {
        // Utility class
    }

    public static List<PropertyDefinition> getProperties() {
        return List.of(
            PropertyDefinition.builder(REPORT_PATH_KEY)
                .name("PHPCS Report Path")
                .description("Path to the generated PHPCS JSON report file")
                .defaultValue(REPORT_PATH_DEFAULT)
                .build()
        );
    }
}
