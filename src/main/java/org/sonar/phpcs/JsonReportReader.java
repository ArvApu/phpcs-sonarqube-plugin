package org.sonar.phpcs;

import javax.annotation.Nullable;
import org.sonarsource.analyzer.commons.internal.json.simple.parser.JSONParser;

public abstract class JsonReportReader {

    public static class Issue {
        @Nullable
        public String filePath;
        @Nullable
        public String message;
        @Nullable
        public String ruleId;
        @Nullable
        public Integer startLine;
        @Nullable
        public Integer startColumn;
        @Nullable
        public Integer endLine;
        @Nullable
        public Integer endColumn;
        @Nullable
        public String type;
        @Nullable
        public String severity;
    }

    protected final JSONParser jsonParser = new JSONParser();

    protected static Integer toInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
}