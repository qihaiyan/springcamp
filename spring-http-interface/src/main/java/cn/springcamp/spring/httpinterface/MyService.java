package cn.springcamp.spring.httpinterface;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface MyService {
    @GetExchange("/anything")
    String getData(@RequestHeader("MY-HEADER") String headerName);

    @GetExchange("/anything/{id}")
    String getData(@PathVariable long id);

    @PostExchange("/anything")
    String saveData(@RequestBody MyData data);

    @DeleteExchange("/anything/{id}")
    ResponseEntity<Void> deleteData(@PathVariable long id);
}
