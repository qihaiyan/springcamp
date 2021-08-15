package cn.springcamp.springeasyrule;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "rule")
public class RuleEngineConfigProperties {
    private boolean skipOnFirstAppliedRule = false;
    private boolean skipOnFirstFailedRule = false;
    private boolean skipOnFirstNonTriggeredRule = false;
    private List<RuleConfig> rules;

    @Data
    public static class RuleConfig {
        private String ruleId;
        private String ruleFileLocation;
    }
}
