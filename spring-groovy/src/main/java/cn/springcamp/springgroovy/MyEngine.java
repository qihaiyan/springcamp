package cn.springcamp.springgroovy;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Slf4j
@Component
public class MyEngine {
    private final GroovyScriptEngine engine;

    @Autowired
    private FunBean funBean;

    public MyEngine() throws IOException {

        engine = new GroovyScriptEngine(ResourceUtils.getFile("classpath:scripts/").getAbsolutePath()
                , this.getClass().getClassLoader());
    }

    public void runScript(int x, int y) throws IllegalAccessException,
            InstantiationException, ResourceException, ScriptException, NoSuchMethodException, InvocationTargetException {
        Class<GroovyObject> calcClass = engine.loadScriptByName("CalcScript.groovy");
        GroovyObject calc = calcClass.getDeclaredConstructor().newInstance();

        Object result = calc.invokeMethod("calcSum", new Object[]{x, y});
        System.out.println("Result of CalcScript.calcSum() method is " + result);

        Binding binding = new Binding();
        binding.setVariable("arg", "test");
        binding.setVariable("funBean", funBean);
        Object result1 = engine.run("CalcScript.groovy", binding);
        System.out.println("Result of CalcScript.groovy is " + result1);
    }
}
