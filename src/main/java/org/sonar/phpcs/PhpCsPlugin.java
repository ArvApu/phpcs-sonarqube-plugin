package org.sonar.phpcs;

import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;

public class PhpCsPlugin implements Plugin {

    public void define(Context context) {
        context.addExtensions(
            PhpCsSensor.class,
            PhpCsRuleDefinition.class,
            PropertyDefinition
                .builder(PhpCsSensor.PHPCS_REPORT_PATH_KEY)
                .name("PHPCodesniffer Report Files")
                .description("Paths (absolute or relative) to report files with PHPCodesniffer issues.")
                .category("External Analyzers")
                .subCategory("PHP")
                .multiValues(true)
                .build()
        );
    }
}
