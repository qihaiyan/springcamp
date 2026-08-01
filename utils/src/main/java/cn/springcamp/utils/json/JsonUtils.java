package cn.springcamp.utils.json;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JsonUtils {
    private static final Supplier<JsonMapper> mapperBuilder = new JacksonObjectMapperBuilder();
    private static final JsonMapper mapper = createMapper();

    private static final JacksonDecorator DELEGATE = new JacksonDecorator(mapper);

    public static JsonMapper createMapper() {
        return mapperBuilder.get();
    }

    public static <V> V readValue(String json, Class<V> valueClass) {
        return print().readValue(json, valueClass);
    }

    public static <V> V readValue(String json, TypeReference<V> valueClass) {
        return print().readValue(json, valueClass);
    }

    public static <V> List<V> readList(String json, Class<V> valueClass) {
        return print().readList(json, valueClass);
    }

    public static <V> List<V> readList(String json, TypeReference<V> valueClass) {
        return print().readList(json, valueClass);
    }

    public static Map<String, ?> readMap(String json) {
        return print().readMap(json);
    }

    public static <V> Map<String, V> readMap(String json, Class<V> valueClass) {
        return print().readMap(json, valueClass);
    }

    public static <K, V> Map<K, V> readMap(String json, Class<K> keyClass, Class<V> valueClass) {
        return print().readMap(json, keyClass, valueClass);
    }

    public static <V> String writeValue(V obj) {
        return print().writeValue(obj);
    }

    public static <V> V convertValue(Object json, TypeReference<V> valueClass) {
        return print().convertValue(json, valueClass);
    }

    public static Map<String, ?> convertMap(Object json) {
        return print().convertMap(json);
    }

    public static <K, V> Map<K, V> convertMap(Object json, Class<K> keyClass, Class<V> valueClass) {
        return print().convertMap(json, keyClass, valueClass);
    }

    public static <V> Map<String, V> convertMap(Object json, Class<V> valueClass) {
        return print().convertMap(json, valueClass);
    }

    public static JacksonDecorator print() {
        return DELEGATE;
    }

    private JsonUtils() {
    }

}
