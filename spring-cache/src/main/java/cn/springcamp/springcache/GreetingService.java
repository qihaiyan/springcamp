package com.example.cache.demo;

//import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
//import com.fasterxml.jackson.dataformat.protobuf.schema.NativeProtobufSchema;
//import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
//import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames="greeting")
public class GreetingService {
    private static final Logger log = LoggerFactory.getLogger(GreetingService.class);

    @Cacheable(key = "#p0")
    public Greeting greeting(Long id) {
//        ProtobufMapper mapper = new ProtobufMapper();
        try {
//            ProtobufSchema schemaWrapper = mapper.generateSchemaFor(Greeting.class);
//            NativeProtobufSchema nativeProtobufSchema = schemaWrapper.getSource();
//            String asProtofile = nativeProtobufSchema.toString();
//            ProtobufSchema schema = ProtobufSchemaLoader.std.parse(asProtofile);
            Greeting greeting = new Greeting(id, "Hello, 你好 Community!");
            Thread.sleep(500);
//            byte[] protobufData = mapper.writer(schema)
//                    .writeValueAsBytes(greeting);
//            Greeting pggreeting = mapper.readerFor(Greeting.class)
//                    .with(schema)
//                    .readValue(protobufData);
//            log.info(pggreeting.toString());
            return greeting;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
