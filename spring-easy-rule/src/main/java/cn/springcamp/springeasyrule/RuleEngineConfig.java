package cn.springcamp.springeasyrule;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.*;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@EnableConfigurationProperties(RuleEngineConfigProperties.class)
@Configuration
public class RuleEngineConfig implements BeanFactoryAware {
    @Autowired(required = false)
    private List<RuleListener> ruleListeners;

    @Autowired(required = false)
    private List<RulesEngineListener> rulesEngineListeners;

    private BeanFactory beanFactory;

    @Bean
    public RulesEngineParameters rulesEngineParameters(RuleEngineConfigProperties properties) {
        RulesEngineParameters parameters = new RulesEngineParameters();
        parameters.setSkipOnFirstAppliedRule(properties.isSkipOnFirstAppliedRule());
        parameters.setSkipOnFirstFailedRule(properties.isSkipOnFirstFailedRule());
        parameters.setSkipOnFirstNonTriggeredRule(properties.isSkipOnFirstNonTriggeredRule());
        return parameters;
    }

    @Bean
    public RulesEngine rulesEngine(RulesEngineParameters rulesEngineParameters) {
        DefaultRulesEngine rulesEngine = new DefaultRulesEngine(rulesEngineParameters);
        if (!CollectionUtils.isEmpty(ruleListeners)) {
            rulesEngine.registerRuleListeners(ruleListeners);
        }
        if (!CollectionUtils.isEmpty(rulesEngineListeners)) {
            rulesEngine.registerRulesEngineListeners(rulesEngineListeners);
        }
        return rulesEngine;
    }

    @Bean
    public BeanResolver beanResolver() {
        return new BeanFactoryResolver(beanFactory);
    }

    @Bean
    public RuleEngineTemplate ruleEngineTemplate(RuleEngineConfigProperties properties, RulesEngine rulesEngine) {
        RuleEngineTemplate ruleEngineTemplate = new RuleEngineTemplate();
        ruleEngineTemplate.setBeanResolver(beanResolver());
        ruleEngineTemplate.setProperties(properties);
        ruleEngineTemplate.setRulesEngine(rulesEngine);
        return ruleEngineTemplate;
    }

    @Bean
    public RuleListener defaultRuleListener() {
        return new RuleListener() {
            @Override
            public boolean beforeEvaluate(Rule rule, Facts facts) {
                return true;
            }

            @Override
            public void afterEvaluate(Rule rule, Facts facts, boolean b) {
                log.info("-----------------afterEvaluate-----------------");
                log.info(rule.getName() + rule.getDescription() + facts.toString());
            }

            @Override
            public void beforeExecute(Rule rule, Facts facts) {
                log.info("-----------------beforeExecute-----------------");
                log.info(rule.getName() + rule.getDescription() + facts.toString());
            }

            @Override
            public void onSuccess(Rule rule, Facts facts) {
                log.info("-----------------onSuccess-----------------");
                log.info(rule.getName() + rule.getDescription() + facts.toString());
            }

            @Override
            public void onFailure(Rule rule, Facts facts, Exception e) {
                log.info("-----------------onFailure-----------------");
                log.info(rule.getName() + "----------" + rule.getDescription() + facts.toString() + e.toString());
            }
        };
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
