package com.example.my_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

    @GetMapping("/{index}/stats")
    public StatsResponse stats(@PathVariable String index) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://opensearch:9200/" + index + "/_search";

        String requestJson = """
            {
              "size": 0,
              "aggs": {
                "avg_battery": { "avg": { "field": "ups_adv_battery_run_time_remaining" } },
                "max_voltage": { "max": { "field": "ups_adv_output_voltage" } },
                "unique_hosts": { "terms": { "field": "host", "size": 1000 } }
              }
            }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        JsonNode aggregations = response.getBody().get("aggregations");

        double avgBattery = aggregations.get("avg_battery").get("value").asDouble();
        double maxVoltage = aggregations.get("max_voltage").get("value").asDouble();

        List<String> hosts = new ArrayList<>();
        aggregations.get("unique_hosts").get("buckets").forEach(bucket -> {
            hosts.add(bucket.get("key").asText());
        });

        return new StatsResponse(avgBattery, maxVoltage, hosts);
    }
}
