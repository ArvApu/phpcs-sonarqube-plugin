package org.sonar.phpcs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.sonarsource.analyzer.commons.internal.json.simple.JSONArray;
import org.sonarsource.analyzer.commons.internal.json.simple.JSONObject;
import org.sonarsource.analyzer.commons.internal.json.simple.parser.ParseException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PhpCsJsonReportReader extends JsonReportReader {

    private final Consumer<Issue> consumer;
    private static final Pattern POSSIBLE_PATH_CONTEXT_PATTERN = Pattern.compile("\\s\\(in context of.*$");

    private PhpCsJsonReportReader(Consumer<Issue> consumer) {
        this.consumer = consumer;
    }

    static void read(InputStream in, Consumer<Issue> consumer) throws IOException, ParseException {
        new PhpCsJsonReportReader(consumer).read(in);
    }

    private void read(InputStream in) throws IOException, ParseException {
        JSONObject rootObject = (JSONObject) jsonParser.parse(new InputStreamReader(in, UTF_8));
        JSONObject files = (JSONObject) rootObject.get("files");
        if (files != null) {
            files.forEach((file, records) -> onFile(cleanFilePath((String) file), (JSONObject) records));
        }
    }

    private void onFile(String file, JSONObject records) {
        JSONArray messages = (JSONArray) records.get("messages");
        if (messages != null) {
            ((Stream<JSONObject>) messages.stream()).forEach(m -> onMessage(file, m));
        }
    }

    /**
     * Here we map phpcs message to and a sonarqube issue
     */
    private void onMessage(String file, JSONObject message) {
        Issue issue = new Issue();
        issue.filePath = file;
        issue.startLine = toInteger(message.get("line"));
        issue.message = (String) message.get("message");
        issue.type = (String) message.get("type"); // BUG, VULNERABILITY, CODE_SMELL
        issue.severity = message.get("severity").toString(); // BLOCKER, CRITICAL, MAJOR, MINOR, INFO
        consumer.accept(issue);
    }

    /**
     * The key containing the file path might contain additional context information when issues are related to traits. Example:
     * <pre>phpstan/file3.php (in context of class Bar)</pre>. We do remove this additional information here.
     * See SONARPHP-1262
     */
    private static String cleanFilePath(String file) {
        return POSSIBLE_PATH_CONTEXT_PATTERN.matcher(file).replaceAll("");
    }
}