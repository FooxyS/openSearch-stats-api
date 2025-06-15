package com.example.my_api;

import java.util.List;

public record StatsResponse(
    double avg_runtime,
    double max_voltage,
    List<String> unique_hosts
) {

}
