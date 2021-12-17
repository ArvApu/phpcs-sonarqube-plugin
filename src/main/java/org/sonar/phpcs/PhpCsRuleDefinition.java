package org.sonar.phpcs;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.ExternalRuleLoader;

import static org.sonar.phpcs.PhpCsSensor.PHPCS_REPORT_KEY;
import static org.sonar.phpcs.PhpCsSensor.PHPCS_REPORT_NAME;

public class PhpCsRuleDefinition implements RulesDefinition {

    private static final String RULES_JSON = "org/sonar/phpcs/rules.json";

    static final ExternalRuleLoader RULE_LOADER = new ExternalRuleLoader(
            PHPCS_REPORT_KEY,
            PHPCS_REPORT_NAME,
            RULES_JSON,
            "php"
    );

    @Override
    public void define(Context context) {
        RULE_LOADER.createExternalRuleRepository(context);
    }
}




