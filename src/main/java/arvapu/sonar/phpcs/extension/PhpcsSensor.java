package arvapu.sonar.phpcs.extension;

import arvapu.sonar.phpcs.extension.sensor.Issue;
import arvapu.sonar.phpcs.extension.sensor.IssueImporter;
import arvapu.sonar.phpcs.extension.sensor.IssueParser;
import arvapu.sonar.phpcs.extension.sensor.UnresolvedInputFileLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PhpcsSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(PhpcsSensor.class);

    private final IssueParser issueParser;
    private final IssueImporter issueImporter;
    private final UnresolvedInputFileLogger unresolvedInputFileLogger;

    public PhpcsSensor(
        IssueParser issueParser,
        IssueImporter issueImporter,
        UnresolvedInputFileLogger unresolvedInputFileLogger
    ) {
        this.issueParser = issueParser;
        this.issueImporter = issueImporter;
        this.unresolvedInputFileLogger = unresolvedInputFileLogger;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .onlyWhenConfiguration((Configuration c) -> c.hasKey(PhpcsProperties.REPORT_PATH_KEY))
            .name("PHPCS Sensor")
            .onlyOnLanguage(PhpcsRulesDefinition.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        // TODO: feature idea: filepath to issue keys that should be ignored (not created in the SQ)
        String reportPath = context.config()
            .get(PhpcsProperties.REPORT_PATH_KEY)
            .orElse(PhpcsProperties.REPORT_PATH_DEFAULT);

        File reportFile = this.getReportFile(context.fileSystem(), reportPath);

        if (!reportFile.exists()) {
            LOG.info("PHPCS report file not found at: {}", reportFile.getAbsolutePath());
            return;
        }

        LOG.info("Processing PHPCS report: {}", reportFile.getAbsolutePath());

        try {
            List<Issue> issues = this.issueParser.parse(reportFile);
            Set<String> unresolvedImportFiles = this.issueImporter.importIssues(context, issues);
            this.unresolvedInputFileLogger.log(unresolvedImportFiles, reportFile);
        } catch (IOException e) {
            LOG.error("Failed to parse PHPCS report {}", reportFile.getAbsolutePath(), e);
        }
    }

    private File getReportFile(FileSystem fileSystem, String reportPath) {
        File report = new File(reportPath);
        if (!report.isAbsolute()) {
            report = new File(fileSystem.baseDir(), reportPath);
        }
        return report;
    }
}
