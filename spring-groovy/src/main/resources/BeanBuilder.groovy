import org.springframework.scripting.groovy.GroovyScriptFactory
import org.springframework.scripting.support.ScriptFactoryPostProcessor

beans {
    scriptFactoryPostProcessor(ScriptFactoryPostProcessor) {
        defaultRefreshCheckDelay = 10000
    }
    myService2(GroovyScriptFactory, 'classpath:MyServiceImpl.groovy') {
        bean ->
            bean.scope = "prototype"
            myProp = ' this is Bean Builder init prop'
            bean.beanDefinition.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, 6000)
    }
}
