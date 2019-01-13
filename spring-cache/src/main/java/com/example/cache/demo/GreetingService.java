package com.example.cache.demo;

import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.fasterxml.jackson.dataformat.protobuf.schema.NativeProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class GreetingService {
    private static final Logger log = LoggerFactory.getLogger(GreetingService.class);

    @Cacheable(cacheNames = "greeting", key = "1")
    public Greeting greeting() {
        ProtobufMapper mapper = new ProtobufMapper();
        try {
            ProtobufSchema schemaWrapper = mapper.generateSchemaFor(Greeting.class);
            NativeProtobufSchema nativeProtobufSchema = schemaWrapper.getSource();
            String asProtofile = nativeProtobufSchema.toString();
            ProtobufSchema schema = ProtobufSchemaLoader.std.parse(asProtofile);
            Greeting greeting = new Greeting(1L, "Hello, 你好 Community!");
            byte[] protobufData = mapper.writer(schema)
                    .writeValueAsBytes(greeting);
            Greeting pggreeting = mapper.readerFor(Greeting.class)
                    .with(schema)
                    .readValue(protobufData);
            log.info(pggreeting.toString());
            return pggreeting;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
