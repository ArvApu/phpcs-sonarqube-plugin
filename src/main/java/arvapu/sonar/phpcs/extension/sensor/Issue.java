package arvapu.sonar.phpcs.extension.sensor;

public record Issue(
    String filePath,
    int line,
    String message,
    String type,
    String source
) {
}
