package cn.springcamp.elasticsearch.javaclient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class DemoService {
    private static final String MY_INDEX = "DemoIndex";
    private ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void init() {
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        elasticsearchClient = new ElasticsearchClient(transport);
    }

    public void save() throws IOException {
        DemoDomain record = new DemoDomain();
        record.setId("1");
        record.setName("test");

        IndexRequest<DemoDomain> indexRequest = IndexRequest.of(b -> b
                .index(MY_INDEX)
                .id(record.getId())
                .document(record)
                .refresh(Refresh.True));  // Make it visible for search

        elasticsearchClient.index(indexRequest);
    }

    public List<DemoDomain> findAll() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(MY_INDEX)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m.term(
                                        t -> t.field("name").value(FieldValue.of("test"))))
                        )
                ));
        SearchResponse<DemoDomain> search = elasticsearchClient.search(searchRequest, DemoDomain.class);
        return search.hits().hits().stream().map(Hit::source).toList();
    }

    public DemoDomain findOne() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(MY_INDEX)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m.term(
                                        t -> t.field("id").value(FieldValue.of("1"))))
                                .must(m -> m.term(
                                        t -> t.field("name").value(FieldValue.of("test"))))
                        )
                ));
        SearchResponse<DemoDomain> search = elasticsearchClient.search(searchRequest, DemoDomain.class);
        return search.hits().hits().get(0).source();
    }

    public void deleteById(String id) throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(s -> s
                .index(MY_INDEX)
                .id(id));
        elasticsearchClient.delete(deleteRequest);
    }

    public void findAndDelete() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(MY_INDEX)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m.term(
                                        t -> t.field("name").value(FieldValue.of("test"))))
                        )
                ));
        SearchResponse<DemoDomain> search = elasticsearchClient.search(searchRequest, DemoDomain.class);
        if (search != null) {
            search.hits().hits().stream().filter(record -> record.source() != null).forEach(record -> {
                DeleteRequest deleteRequest = DeleteRequest.of(s -> s
                        .index(MY_INDEX)
                        .id(record.source().getId()));
                try {
                    elasticsearchClient.delete(deleteRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}