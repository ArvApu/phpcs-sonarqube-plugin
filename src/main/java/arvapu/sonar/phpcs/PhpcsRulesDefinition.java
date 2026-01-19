package arvapu.sonar.phpcs;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.ExternalRuleLoader;

import javax.annotation.Nonnull;

public class PhpcsRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "phpcs";

    private static final String REPOSITORY_NAME = "PHPCS";
    private static final String LANGUAGE_KEY = "php";
    private static final String RULES_JSON = "arvapu/sonar/phpcs/rules.json";

    public static final ExternalRuleLoader RULE_LOADER = new ExternalRuleLoader(
        REPOSITORY_KEY,
        REPOSITORY_NAME,
        RULES_JSON,
        LANGUAGE_KEY,
        null
    );

    @Override
    public void define(@Nonnull Context context) {
        RULE_LOADER.createExternalRuleRepository(context);
    }
}
