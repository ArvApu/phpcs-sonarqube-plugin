package arvapu.sonar.phpcs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewExternalIssue;
import org.sonar.api.config.Configuration;
import org.sonar.api.rules.RuleType;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class PhpcsSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(PhpcsSensor.class);
    private static final String FALLBACK_RULE_KEY = "phpcs.default";

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .onlyWhenConfiguration((Configuration c) -> c.hasKey(PhpcsProperties.REPORT_PATH_KEY))
            .name("PHPCS Sensor")
            .onlyOnLanguage("php");
    }

    @Override
    public void execute(SensorContext context) {
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
            this.parseAndImportIssues(context, reportFile);
        } catch (IOException e) {
            LOG.error("Failed to parse PHPCS report", e);
        }
    }

    private File getReportFile(FileSystem fs, String reportPath) {
        File report = new File(reportPath);
        if (!report.isAbsolute()) {
            report = new File(fs.baseDir(), reportPath);
        }
        return report;
    }

    private void parseAndImportIssues(SensorContext context, File reportFile) throws IOException {
        Gson gson = new Gson(); // TODO: DI?

        JsonObject root = gson.fromJson(new FileReader(reportFile), JsonObject.class);

        JsonObject files = root.getAsJsonObject("files");

        if (files == null) {
            LOG.warn("No 'files' object found in PHPCS report");

            return;
        }

        FileSystem fs = context.fileSystem();

        int issuesImported = 0;

        for (String filePath : files.keySet()) {
            JsonObject fileData = files.getAsJsonObject(filePath);
            JsonArray messages = fileData.getAsJsonArray("messages");

            if (messages == null || messages.isEmpty()) {
                continue;
            }

            InputFile inputFile = this.findInputFile(fs, filePath);

            if (inputFile == null) {
                // TODO: collect unresolved files for logging later (as in ExternalIssuesSensor (see code history)))
                LOG.debug("Could not find input file for: {}", filePath);
                continue;
            }

            for (JsonElement messageElement : messages) {
                JsonObject message = messageElement.getAsJsonObject();
                this.createIssue(context, inputFile, message);
                issuesImported++;
            }
        }

        LOG.info("Imported {} PHPCS issues", issuesImported);
    }

    private InputFile findInputFile(FileSystem fs, String filePath) {
        // Try direct path first
        InputFile inputFile = fs.inputFile(
            fs.predicates().hasAbsolutePath(filePath)
        );

        if (inputFile == null) {
            // Try relative to base directory
            String relativePath = fs.baseDir().toPath()
                .relativize(Paths.get(filePath))
                .toString();

            inputFile = fs.inputFile(
                fs.predicates().hasRelativePath(relativePath)
            );
        }

        return inputFile;
    }

    private void createIssue(SensorContext context, InputFile inputFile, JsonObject message) {
        int line = message.get("line").getAsInt();
        String messageText = message.get("message").getAsString();
        String source = message.get("source").getAsString();
        String type = message.get("type").getAsString();

        // Map PHPCS severity: ERROR or WARNING
        Severity severity = "ERROR".equals(type) ? Severity.MAJOR : Severity.MINOR;

        // Check if rule exists, otherwise use fallback
        String ruleId = this.getRuleIdFromSource(source);
        String issueMessage = messageText;

        if (ruleId.equals(FALLBACK_RULE_KEY)) {
            LOG.debug("Rule {} not found in rules.json, using fallback rule", source);
            issueMessage = String.format("%s (%s)", messageText, source);
        }

        NewExternalIssue issue = context.newExternalIssue()
            .engineId(PhpcsRulesDefinition.REPOSITORY_KEY)
            .ruleId(ruleId)
            .type(RuleType.CODE_SMELL)
            .severity(severity)
            .remediationEffortMinutes(5L);

        issue.at(
            issue.newLocation()
                .on(inputFile)
                .at(inputFile.selectLine(line))
                .message(issueMessage)
        );

        issue.save();
    }

    private String getRuleIdFromSource(@Nullable String source) {
        return source != null && PhpcsRulesDefinition.RULE_LOADER.ruleKeys().contains(source)
            ? source
            : FALLBACK_RULE_KEY;
    }
}
