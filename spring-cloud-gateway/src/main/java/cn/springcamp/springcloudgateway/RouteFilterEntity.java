package cn.springcamp.springcloudgateway;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class RouteFilterEntity {
    @Id
    private String id;
    private String routeId;
    private String code;
    private String url;
}
