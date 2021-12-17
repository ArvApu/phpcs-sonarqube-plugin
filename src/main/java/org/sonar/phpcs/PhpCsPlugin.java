package org.sonar.phpcs;

import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class PhpCsPlugin implements Plugin {

    private static final Logger LOG = Loggers.get(PhpCsSensor.class);

    public void define(Context context) {

        LOG.info("Defining plugin.");

        context.addExtensions(
                PhpCsSensor.class,
                PropertyDefinition
                        .builder(PhpCsSensor.PHPCS_REPORT_PATH_KEY)
                        .name("PHPCodesniffer Report Files")
                        .description("Paths (absolute or relative) to report files with PHPCodesniffer issues.")
                        .category("External Analyzers")
                        .subCategory("PHP")
                        .onQualifiers(Qualifiers.PROJECT)
                        .multiValues(true)
                        .build(),
                PhpCsRuleDefinition.class
        );

        LOG.info("Plugin defined.");
    }
}
