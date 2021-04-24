import cn.springcamp.springgroovy.FunBean
import cn.springcamp.springgroovy.MyDomain
import cn.springcamp.springgroovy.MyService
import org.springframework.beans.factory.annotation.Autowired

class MyServiceImpl implements MyService {
    @Autowired
    FunBean useBean;

    String fun(MyDomain myDomain) {
        return myDomain.toString() + useBean.getFunName();
    }
}
