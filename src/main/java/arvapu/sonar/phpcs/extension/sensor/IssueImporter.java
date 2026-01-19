package arvapu.sonar.phpcs.extension.sensor;

import arvapu.sonar.phpcs.extension.PhpcsRulesDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewExternalIssue;
import org.sonar.api.rules.RuleType;
import org.sonar.api.scanner.ScannerSide;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ScannerSide
public class IssueImporter {
    private static final Logger LOG = LoggerFactory.getLogger(IssueImporter.class);
    private static final String FALLBACK_RULE_KEY = "phpcs.default";
    private static final long DEFAULT_CONSTANT_DEBT_MINUTES = 5L;

    private final InputFileLocator inputFileLocator;

    public IssueImporter(InputFileLocator inputFileLocator) {
        this.inputFileLocator = inputFileLocator;
    }

    /**
     * @return Set of file paths that were unresolved during import
     */
    public Set<String> importIssues(SensorContext context, List<Issue> issues) {
        Set<String> unresolvedInputFiles = new LinkedHashSet<>();

        FileSystem contextFileSystem = context.fileSystem();

        int issuesImported = 0;

        for (Issue issue : issues) {
            InputFile inputFile = this.inputFileLocator.findInputFile(contextFileSystem, issue.filePath());

            if (inputFile == null) {
                unresolvedInputFiles.add(issue.filePath());
                LOG.debug("Could not find input file for: {}", issue.filePath());

                continue;
            }

            this.saveIssue(context, inputFile, issue);

            issuesImported++;
        }

        LOG.info("Imported {} PHPCS issues", issuesImported);

        return unresolvedInputFiles;
    }

    private void saveIssue(SensorContext context, InputFile inputFile, Issue issue) {
        String ruleId = this.getRuleIdFromSource(issue.source());
        String issueMessage = issue.message();

        if (ruleId.equals(FALLBACK_RULE_KEY)) {
            LOG.debug("Rule {} not found in rules.json, using fallback rule", issue.source());
            issueMessage = String.format("%s (%s)", issueMessage, issue.source());
        }

        NewExternalIssue newExternalIssue = context.newExternalIssue()
            .engineId(PhpcsRulesDefinition.REPOSITORY_KEY)
            .ruleId(ruleId)
            .type(RuleType.CODE_SMELL)
            .severity("ERROR".equalsIgnoreCase(issue.type()) ? Severity.MAJOR : Severity.MINOR)
            .remediationEffortMinutes(DEFAULT_CONSTANT_DEBT_MINUTES);

        newExternalIssue.at(
            newExternalIssue.newLocation()
                .on(inputFile)
                .at(this.safeGetSelectedLine(inputFile, issue.line()))
                .message(issueMessage)
        );

        newExternalIssue.save();
    }

    private TextRange safeGetSelectedLine(InputFile inputFile, int line) {
        try {
            return inputFile.selectLine(line);
        } catch (IllegalArgumentException e) {
            LOG.warn("Line {} does not exist in file {}, defaulting to first line", line, inputFile.uri(), e);

            return inputFile.selectLine(1);
        }
    }

    private String getRuleIdFromSource(@Nullable String source) {
        return source != null && PhpcsRulesDefinition.isKnownRule(source) ? source : FALLBACK_RULE_KEY;
    }
}
