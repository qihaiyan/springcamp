package cn.springcamp.jsonutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Slf4j
public class JacksonDecorator {
    protected final Supplier<ObjectMapper> supplier;

    public JacksonDecorator(ObjectMapper mapper) {
        this(() -> mapper);
    }

    public JacksonDecorator(Supplier<ObjectMapper> supplier) {
        this.supplier = supplier;
    }

    private static <V> V withRuntimeException(Callable<V> task) {
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <V> V withNullableException(Callable<V> task) {
        try {
            return task.call();
        } catch (Exception e) {
            log.error("JacksonDecorator error", e);
            return null;
        }
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    private static boolean isEmpty(String json) {
        json = json.trim();
        return "{}".equals(json) || "[]".equals(json);
    }

    public <V> V readValue(String json, Class<V> valueClass) {
        Objects.requireNonNull(valueClass, "'valueClass' should not be null");

        if (json == null || json.isEmpty())
            return null;

        return withRuntimeException(() -> supplier.get().readValue(json, valueClass));
    }

    public <V> V readValue(String json, TypeReference<V> valueClass) {
        Objects.requireNonNull(valueClass, "'valueClass' should not be null");

        if (json == null || json.isEmpty())
            return null;

        return withRuntimeException(() -> supplier.get().readValue(json, valueClass));
    }

    public <V> List<V> readList(String json, Class<V> valueClass) {
        Objects.requireNonNull(valueClass, "'valueClass' should not be null");

        if (json == null)
            return null;
        if (isEmpty(json))
            return Collections.emptyList();

        return withRuntimeException(() -> {
            ObjectReader reader = supplier.get().readerFor(valueClass);
            return reader.<V>readValues(json).readAll();
        });
    }

    public <V> List<V> readList(String json, TypeReference<V> valueClass) {
        Objects.requireNonNull(valueClass, "'valueClass' should not be null");

        if (json == null)
            return null;
        if (isEmpty(json))
            return Collections.emptyList();

        return withRuntimeException(() -> {
            ObjectReader reader = supplier.get().readerFor(valueClass);
            return reader.<V>readValues(json).readAll();
        });
    }

    public Map<String, ?> readMap(String json) {
        if (json == null)
            return null;
        if (isEmpty(json))
            return Collections.emptyMap();

        return withRuntimeException(() -> {
            ObjectMapper mapper = supplier.get();
            MapType mapType = mapper.getTypeFactory().constructRawMapType(LinkedHashMap.class);
            return mapper.readValue(json, mapType);
        });
    }

    public <V> Map<String, V> readMap(String json, Class<V> valueClass) {
        return readMap(json, String.class, valueClass);
    }

    public <K, V> Map<K, V> readMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (json == null)
            return null;
        if (isEmpty(json))
            return Collections.emptyMap();

        return withRuntimeException(() -> {
            ObjectMapper mapper = supplier.get();
            MapType mapType = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, keyClass, valueClass);
            return mapper.readValue(json, mapType);
        });
    }

    public <V> String writeValue(V obj) {
        if (obj == null)
            return null;

        return withRuntimeException(() -> supplier.get().writeValueAsString(obj));
    }

    public <V> V convertValue(Object json, TypeReference<V> valueClass) {
        Objects.requireNonNull(valueClass, "'valueClass' should not be null");

        if (json == null)
            return null;

        return withNullableException(() -> supplier.get().convertValue(json, valueClass));
    }

    public Map<String, ?> convertMap(Object json) {
        if (json == null)
            return null;

        return withNullableException(() -> {
            ObjectMapper mapper = supplier.get();
            MapType mapType = mapper.getTypeFactory().constructRawMapType(LinkedHashMap.class);
            return mapper.convertValue(json, mapType);
        });
    }

    public <K, V> Map<K, V> convertMap(Object json, Class<K> keyClass, Class<V> valueClass) {
        if (json == null)
            return null;

        return withNullableException(() -> {
            ObjectMapper mapper = supplier.get();
            MapType mapType = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, keyClass, valueClass);
            return mapper.convertValue(json, mapType);
        });
    }

    public <V> Map<String, V> convertMap(Object json, Class<V> valueClass) {
        return convertMap(json, String.class, valueClass);
    }

}
