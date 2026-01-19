package arvapu.sonar.phpcs;

import arvapu.sonar.phpcs.extension.PhpcsProperties;
import arvapu.sonar.phpcs.extension.PhpcsRulesDefinition;
import arvapu.sonar.phpcs.extension.PhpcsSensor;
import arvapu.sonar.phpcs.extension.sensor.InputFileLocator;
import arvapu.sonar.phpcs.extension.sensor.IssueImporter;
import arvapu.sonar.phpcs.extension.sensor.IssueParser;
import arvapu.sonar.phpcs.extension.sensor.UnresolvedInputFileLogger;
import org.sonar.api.Plugin;

public class PhpcsPlugin implements Plugin {
    @Override
    public void define(Context context) {
        context.addExtensions(
            // Server-side extensions
            PhpcsRulesDefinition.class,
            PhpcsProperties.class,
            // Scanner-side extensions (must be annotated with @ScannerSide)
            PhpcsSensor.class,
            IssueParser.class,
            IssueImporter.class,
            InputFileLocator.class,
            UnresolvedInputFileLogger.class
        );
    }
}
