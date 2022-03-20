package cn.springcamp.springboot.unit.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;

public class TestRestResponseSlice<T> extends SliceImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TestRestResponseSlice(@JsonProperty("content") List<T> content,
                                 @JsonProperty("number") int number,
                                 @JsonProperty("size") int size,
                                 @JsonProperty("pageable") JsonNode pageable,
                                 @JsonProperty("last") boolean last,
                                 @JsonProperty("sort") JsonNode sort,
                                 @JsonProperty("first") boolean first,
                                 @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size), false);
    }

//    public TestRestResponseSlice(List<T> content, Pageable pageable, long total) {
//        super(content, pageable, total);
//    }

    public TestRestResponseSlice(List<T> content) {
        super(content);
    }

    public TestRestResponseSlice() {
        super(new ArrayList<>());
    }
}
