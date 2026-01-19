package arvapu.sonar.phpcs;

import org.sonar.api.Plugin;

public class PhpcsPlugin implements Plugin {
    @Override
    public void define(Context context) {
        context.addExtensions(
            PhpcsRulesDefinition.class,
            PhpcsSensor.class,
            PhpcsProperties.class
        );
    }
}
