package cn.springcamp.springdata.jdbc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DbService {
    @Autowired
    private JdbcClient jdbcClient;

    public Integer insertDataWithParam(MyData myData) {
        Integer rowsAffected = jdbcClient.sql("insert into my_data values(?,?) ")
                .param(myData.id())
                .param(myData.name())
                .update();
        return rowsAffected;
    }

    public Integer insertDataWithNamedParam(MyData myData) {
        Integer rowsAffected = jdbcClient.sql("insert into my_data values(:id,:name) ")
                .param("id", myData.id())
                .param("name", myData.name())
                .update();
        return rowsAffected;
    }

    public Integer insertDataWithObject(MyData myData) {
        Integer rowsAffected = jdbcClient.sql("insert into my_data values(:id,:name) ")
                .paramSource(myData)
                .update();
        return rowsAffected;
    }

    public MyData findDataById(Long id) {
        return jdbcClient.sql("select * from my_data where id = ?")
                .params(id)
                .query(MyData.class)
                .single();
    }

    public List<MyData> findDataByName(String name) {
        return jdbcClient.sql("select * from my_data where name = ?")
                .params(name)
                .query(MyData.class)
                .list();
    }

    public List<MyData> findDataByParamMap(Map<String, ?> paramMap) {
        return jdbcClient.sql("select * from my_data where name = :name")
                .params(paramMap)
                .query(MyData.class)
                .list();
    }

    public List<MyData> findDataWithRowMapper() {
        return jdbcClient.sql("select * from my_data")
                .query((rs, rowNum) -> new MyData(rs.getLong("id"), rs.getString("name")))
                .list();
    }

    public Integer countByName(String name) {
        return jdbcClient.sql("select count(*) from my_data where name = ?")
                .params(name)
                .query(Integer.class)
                .single();
    }
}
