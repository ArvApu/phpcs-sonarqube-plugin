package org.sonar.phpcs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonarsource.analyzer.commons.ExternalRuleLoader;
import org.sonarsource.analyzer.commons.internal.json.simple.parser.ParseException;

public class PhpCsSensor extends ExternalIssuesSensor {
    private static final Logger LOG = LoggerFactory.getLogger(PhpCsSensor.class);

    public static final String PHPCS_REPORT_KEY = "phpcs";
    public static final String PHPCS_REPORT_NAME = "PHPCS";
    public static final String PHPCS_REPORT_PATH_KEY = "sonar.php.phpcs.reportPaths";

    public PhpCsSensor() {
    }

    protected void importReport(File reportPath, SensorContext context) throws IOException, ParseException {
        LOG.info("Importing {}", reportPath);
        PhpCsJsonReportReader.read(new FileInputStream(reportPath), issue -> saveIssue(context, issue));
    }

    protected String reportName() {
        return PHPCS_REPORT_NAME;
    }

    protected String reportKey() {
        return PHPCS_REPORT_KEY;
    }

    protected String reportPathKey() {
        return PHPCS_REPORT_PATH_KEY;
    }

    protected Logger logger() {
        return LOG;
    }

    protected ExternalRuleLoader externalRuleLoader() {
        return PhpCsRuleDefinition.RULE_LOADER;
    }
}
