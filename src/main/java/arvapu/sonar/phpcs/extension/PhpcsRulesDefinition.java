package arvapu.sonar.phpcs.extension;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.ExternalRuleLoader;

import javax.annotation.Nonnull;
import java.util.Set;

public class PhpcsRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "phpcs";
    public static final String LANGUAGE_KEY = "php";

    private static final String REPOSITORY_NAME = "PHPCS";
    private static final String RULES_JSON_PATH = "arvapu/sonar/phpcs/rules.json";

    private static final ExternalRuleLoader RULE_LOADER =
        new ExternalRuleLoader(
            REPOSITORY_KEY,
            REPOSITORY_NAME,
            RULES_JSON_PATH,
            LANGUAGE_KEY,
            null
        );

    private static final Set<String> RULE_KEYS = RULE_LOADER.ruleKeys();

    public static boolean isKnownRule(@Nonnull String ruleKey) {
        return RULE_KEYS.contains(ruleKey);
    }

    @Override
    public void define(@Nonnull Context context) {
        RULE_LOADER.createExternalRuleRepository(context);
    }
}
