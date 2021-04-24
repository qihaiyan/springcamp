import org.springframework.scripting.groovy.GroovyScriptFactory;

beans {
    myService2(GroovyScriptFactory, 'classpath:MyServiceImpl.groovy') {
    }
}
