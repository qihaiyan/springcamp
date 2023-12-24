package cn.springcamp.spring.httpinterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class MyController {
    @Autowired
    private MyService myService;

    @GetMapping("/foo")
    public String getData() {
        return myService.getData("myHeader");
    }

    @GetMapping("/foo/{id}")
    public String getDataById(@PathVariable Long id) {
        return myService.getData(id);
    }

    @PostMapping("/foo")
    public String saveData() {
        return myService.saveData(new MyData(1L, "demo"));
    }

    @DeleteMapping("/foo")
    public ResponseEntity<Void> deleteData() {
        ResponseEntity<Void> resp = myService.deleteData(1L);
        log.info("delete {}", resp);
        return resp;
    }
}
