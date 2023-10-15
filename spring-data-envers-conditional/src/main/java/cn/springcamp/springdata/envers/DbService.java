package cn.springcamp.springdata.envers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Component;

@Component
public class DbService {
    @Autowired
    private MyDataRepository myDataRepository;

    public void saveData(MyData myData) {
        myDataRepository.save(myData);
    }

    public Revisions<Integer, MyData> findRevisions(Long id) {
        return myDataRepository.findRevisions(id);
    }
}
