package arvapu.sonar.phpcs.extension.sensor;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

import java.nio.file.Paths;

public class InputFileLocator {

    public InputFile findInputFile(FileSystem fileSystem, String filePath) {
        InputFile inputFile = fileSystem.inputFile(
            fileSystem.predicates().hasAbsolutePath(filePath)
        );

        // We want to also check, relative path when absolute (direct) path lookup fails
        if (inputFile == null) {
            String relativePath = this.determineRelativePath(fileSystem, filePath);

            inputFile = fileSystem.inputFile(
                fileSystem.predicates().hasRelativePath(relativePath)
            );
        }

        return inputFile;
    }

    private String determineRelativePath(FileSystem fileSystem, String filePath) {
        return fileSystem.baseDir().toPath()
            .relativize(Paths.get(filePath))
            .toString();
    }
}
