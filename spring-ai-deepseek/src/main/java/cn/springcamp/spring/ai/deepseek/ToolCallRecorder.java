package cn.springcamp.spring.ai.deepseek;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 记录单次 HTTP 请求内的工具调用过程（工具名、入参、返回值）。
 * 使用 @RequestScope：每个 HTTP 请求创建一个独立实例，请求结束后销毁，
 * 无需 ThreadLocal，对虚拟线程友好。
 */
@Component
@RequestScope
public class ToolCallRecorder {

    private final List<ToolInvocation> invocations = new ArrayList<>();

    public record ToolInvocation(String tool, String arguments, String result) {
    }

    public void record(String tool, String arguments, String result) {
        invocations.add(new ToolInvocation(tool, arguments, result));
    }

    public List<ToolInvocation> get() {
        return Collections.unmodifiableList(invocations);
    }
}
