package cn.springcamp.utils.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Supplier;

public class JacksonObjectMapperBuilder implements Supplier<JsonMapper> {

    @Override
    public JsonMapper get() {
        // Jackson 3 merged the JavaTime/Jdk8/ParameterNames modules into the core, so they
        // no longer need to be registered explicitly. Afterburner has been replaced by built-in
        // bytecode handling; no registration is needed.
        return JsonMapper.builder()
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                        .withVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                        .withVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                        .withVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.ANY)
                        .withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                        .withVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY))
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
