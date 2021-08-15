package cn.springcamp.springeasyrule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.spel.SpELRuleFactory;
import org.jeasy.rules.support.reader.RuleDefinitionReader;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.BeanResolver;
import org.springframework.util.ResourceUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class RuleEngineTemplate implements InitializingBean {

    private RuleEngineConfigProperties properties;

    private RulesEngine rulesEngine;

    private BeanResolver beanResolver;

    private Map<String, Rules> rules = new HashMap<>();

    public void fire(String ruleId, Facts facts) {
        Rules rules = this.rules.get(ruleId);
        if (rules == null) {
            throw new RuntimeException("rule id: " + ruleId + "not define, please check");
        }
        rulesEngine.fire(rules, facts);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (RuleEngineConfigProperties.RuleConfig ruleConfig : properties.getRules()) {
            RuleDefinitionReader ruleDefinitionReader;
            ruleDefinitionReader = new YamlRuleDefinitionReader();
            SpELRuleFactory spELRuleFactory = new SpELRuleFactory(ruleDefinitionReader, beanResolver);
            Rules rule = spELRuleFactory.createRules(new InputStreamReader(ResourceUtils.getURL(ruleConfig.getRuleFileLocation()).openStream(), StandardCharsets.UTF_8.displayName()));
            rules.put(ruleConfig.getRuleId(), rule);
        }
    }
}
