package arvapu.sonar.phpcs.extension.sensor;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IssueParserTest {
    @Test
    public void parsesValidPhpcsMessage() throws IOException {
        String json = """
        {
          "files": {
            "src/Foo.php": {
              "messages": [
                {
                  "message": "Line exceeds 120 characters; contains 128 characters",
                  "source": "Generic.Files.LineLength.TooLong",
                  "severity": 5,
                  "fixable": false,
                  "type": "WARNING",
                  "line": 37,
                  "column": 128
                }
              ]
            }
          }
        }
        """;
        File file = this.createTempJsonFile(json);
        IssueParser parser = new IssueParser();
        List<Issue> issues = parser.parse(file);

        assertEquals(1, issues.size());
        Issue issue = issues.get(0);
        assertEquals("src/Foo.php", issue.filePath());
        assertEquals(37, issue.line());
        assertEquals("Line exceeds 120 characters; contains 128 characters", issue.message());
        assertEquals("WARNING", issue.type());
        assertEquals("Generic.Files.LineLength.TooLong", issue.source());
    }

    @Test
    public void returnsEmptyListIfNoFiles() throws IOException {
        String json = """
        {
          "files": {}
        }
        """;
        File file = this.createTempJsonFile(json);
        IssueParser parser = new IssueParser();
        List<Issue> issues = parser.parse(file);

        assertTrue(issues.isEmpty());
    }

    @Test
    public void returnsEmptyListIfNoMessages() throws IOException {
        String json = """
        {
          "files": {
            "src/Bar.php": {
              "messages": []
            }
          }
        }
        """;
        File file = this.createTempJsonFile(json);
        IssueParser parser = new IssueParser();
        List<Issue> issues = parser.parse(file);

        assertTrue(issues.isEmpty());
    }

    @Test
    public void throwsExceptionOnInvalidJson() throws IOException {
        String json = "not a json";
        File file = this.createTempJsonFile(json);
        IssueParser parser = new IssueParser();

        assertThrows(Exception.class, () -> parser.parse(file));
    }

    private File createTempJsonFile(String json) throws IOException {
        File tempFile = File.createTempFile("phpcs-issue", ".json");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(json);
        }
        tempFile.deleteOnExit();
        return tempFile;
    }
}
