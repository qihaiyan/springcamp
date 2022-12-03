package cn.springcamp.springnative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbService {
    @Autowired
    private TestDataRepository testDataRepository;

    public DemoData hello() {
        DemoData demoData = new DemoData();
        demoData = testDataRepository.save(demoData);
        return demoData;
    }
}
