package arvapu.sonar.phpcs.extension.sensor;

import arvapu.sonar.phpcs.extension.PhpcsRulesDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class UnresolvedInputFileLogger {
    private static final Logger LOG = LoggerFactory.getLogger(UnresolvedInputFileLogger.class);
    private static final int MAX_LOGGED_FILE_NAMES = 5;

    public void log(Set<String> files, File reportPath) {
        if (files.isEmpty()) {
            return;
        }

        String fileList = files.stream().sorted().limit(MAX_LOGGED_FILE_NAMES).collect(Collectors.joining(";"));

        if (files.size() > MAX_LOGGED_FILE_NAMES) {
            fileList += ";...";
        }

        String message = String.format(
            "Failed to resolve %s file path(s) in %s %s report. No issues imported related to file(s): %s",
            files.size(),
            PhpcsRulesDefinition.REPOSITORY_KEY,
            reportPath.getName(),
            fileList
        );

        LOG.warn(message);
    }
}
