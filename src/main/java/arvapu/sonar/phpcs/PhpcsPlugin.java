package arvapu.sonar.phpcs;

import arvapu.sonar.phpcs.extension.PhpcsProperties;
import arvapu.sonar.phpcs.extension.PhpcsRulesDefinition;
import arvapu.sonar.phpcs.extension.PhpcsSensor;
import arvapu.sonar.phpcs.extension.sensor.InputFileLocator;
import arvapu.sonar.phpcs.extension.sensor.IssueImporter;
import arvapu.sonar.phpcs.extension.sensor.IssueParser;
import arvapu.sonar.phpcs.extension.sensor.UnresolvedInputFileLogger;
import com.google.gson.Gson;
import org.sonar.api.Plugin;

public class PhpcsPlugin implements Plugin {
    @Override
    public void define(Context context) {
        context.addExtensions(
            PhpcsRulesDefinition.class,
            PhpcsSensor.class,
            PhpcsProperties.class,
            IssueParser.class,
            IssueImporter.class,
            Gson.class,
            InputFileLocator.class,
            UnresolvedInputFileLogger.class
        );
    }
}
