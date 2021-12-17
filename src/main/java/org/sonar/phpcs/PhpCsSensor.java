package org.sonar.phpcs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.analyzer.commons.ExternalRuleLoader;
import org.sonarsource.analyzer.commons.internal.json.simple.parser.ParseException;

public class PhpCsSensor extends ExternalIssuesSensor {

    private static final Logger LOG = Loggers.get(PhpCsSensor.class);

    public static final String PHPCS_REPORT_KEY = "phpcs";
    public static final String PHPCS_REPORT_NAME = "PHPCodesniffer";
    public static final String PHPCS_REPORT_PATH_KEY = "sonar.php.phpcs.reportPaths";

    public PhpCsSensor() {
    }

    protected void importReport(File reportPath, SensorContext context) throws IOException, ParseException {
        InputStream in = new FileInputStream(reportPath);
        LOG.info("Importing {}", reportPath);
        PhpCsJsonReportReader.read(in, issue -> saveIssue(context, issue));
    }

    protected String reportName() {
        return PhpCsSensor.PHPCS_REPORT_NAME;
    }

    protected String reportKey() {
        return PhpCsSensor.PHPCS_REPORT_KEY;
    }

    protected String reportPathKey() {
        return PhpCsSensor.PHPCS_REPORT_PATH_KEY;
    }

    protected Logger logger() {
        return LOG;
    }

    protected ExternalRuleLoader externalRuleLoader() {
        return PhpCsRuleDefinition.RULE_LOADER;
    }
}
