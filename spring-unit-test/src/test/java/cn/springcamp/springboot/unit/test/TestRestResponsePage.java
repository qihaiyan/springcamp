package cn.springcamp.springboot.unit.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public class TestRestResponsePage<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TestRestResponsePage(@JsonProperty("content") List<T> content,
                                @JsonProperty("number") int number,
                                @JsonProperty("size") int size,
                                @JsonProperty("pageable") JsonNode pageable,
                                @JsonProperty("empty") boolean empty,
                                @JsonProperty("sort") JsonNode sort,
                                @JsonProperty("first") boolean first,
                                @JsonProperty("totalElements") long totalElements,
                                @JsonProperty("totalPages") int totalPages,
                                @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public TestRestResponsePage(List<T> content) {
        super(content);
    }

    public TestRestResponsePage() {
        super(new ArrayList<>());
    }
}
