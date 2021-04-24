package cn.springcamp.springgroovy;

import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Component
public class MyEngine {
//    private final GroovyScriptEngine engine;
//
//    public MyEngine() {
//        URL url = null;
//        try {
//            url = new File("src/main/groovy/com/baeldung/").toURI().toURL();
//        } catch (MalformedURLException ignored) {
//
//        }
//        engine = new GroovyScriptEngine(new URL[]{url}, this.getClass().getClassLoader());
//    }
//
//    private void addWithGroovyScriptEngine(int x, int y) throws IllegalAccessException,
//            InstantiationException, ResourceException, ScriptException {
//        Class<GroovyObject> calcClass = engine.loadScriptByName("CalcMath.groovy");
//        GroovyObject calc = calcClass.newInstance();
//        Object result = calc.invokeMethod("calcSum", new Object[]{x, y});
//        log.info("Result of CalcMath.calcSum() method is {}", result);
//    }
}
