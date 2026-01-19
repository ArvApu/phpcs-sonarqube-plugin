package arvapu.sonar.phpcs.extension.sensor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.scanner.ScannerSide;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ScannerSide
public class IssueParser {
    private static final Logger LOG = LoggerFactory.getLogger(IssueParser.class);

    private final Gson gson;

    public IssueParser() {
        // Not going to deal with third party DI at the moment
        this.gson = new Gson();
    }

    public List<Issue> parse(File reportFile) throws IOException {
        List<Issue> issues = new ArrayList<>();

        JsonObject files = this.gson
            .fromJson(new FileReader(reportFile), JsonObject.class)
            .getAsJsonObject("files");

        if (files == null) {
            LOG.warn("No 'files' object found in PHPCS report");

            return issues;
        }

        for (String filePath : files.keySet()) {
            JsonObject fileData = files.getAsJsonObject(filePath);
            JsonArray messages = fileData.getAsJsonArray("messages");

            if (messages == null || messages.isEmpty()) {
                continue;
            }

            for (JsonElement messageElement : messages) {
                JsonObject message = messageElement.getAsJsonObject();

                issues.add(new Issue(
                    filePath,
                    message.get("line").getAsInt(),
                    message.get("message").getAsString(),
                    message.get("type").getAsString(),
                    message.get("source").getAsString()
                ));
            }
        }

        return issues;
    }
}
