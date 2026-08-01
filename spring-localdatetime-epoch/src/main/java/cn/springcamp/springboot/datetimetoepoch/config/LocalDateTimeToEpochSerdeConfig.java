package cn.springcamp.springboot.datetimetoepoch.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.deser.jdk.NumberDeserializers;
import tools.jackson.databind.module.SimpleModule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
public class LocalDateTimeToEpochSerdeConfig {

    @Bean
    public JsonMapperBuilderCustomizer localDateTimeEpochCustomizer() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeToEpochSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeFromEpochDeserializer());
        return builder -> builder.addModule(module);
    }

    /**
     * 序列化
     */
    public static class LocalDateTimeToEpochSerializer extends ValueSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt)
                throws JacksonException {
            if (value != null) {
                long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
                gen.writeNumber(timestamp);
            }
        }
    }

    /**
     * 反序列化
     */
    public static class LocalDateTimeFromEpochDeserializer extends ValueDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            NumberDeserializers.LongDeserializer longDeserializer = new NumberDeserializers.LongDeserializer(Long.TYPE, 0L);
            Long epoch = longDeserializer.deserialize(p, ctxt);
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault());
        }
    }
}
