package cn.springcamp.springnative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbService {
    @Autowired
    private DemoDataRepository demoDataRepository;

    public DemoData hello() {
        DemoData demoData = new DemoData();
        demoData.setName("test");
        demoData = demoDataRepository.save(demoData);
        demoDataRepository.insert0(demoData);
        demoDataRepository.insert1(demoData);
        return demoData;
    }
}
