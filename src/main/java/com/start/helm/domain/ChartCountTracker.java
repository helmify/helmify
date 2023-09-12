package com.start.helm.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class ChartCountTracker {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    private File getStore() {
        Path path = Paths.get("chart-count.json");
        if (!Files.exists(path)) {
            Files.createFile(path);
            String json = this.objectMapper.writeValueAsString(new ChartCount(0));
            Files.write(path, json.getBytes());
        }
        return path.toFile();
    }

    @SneakyThrows
    public int getChartCount() {
        File store = this.getStore();
        return this.objectMapper.readValue(store, ChartCount.class).getChartsGenerated();
    }

    @SneakyThrows
    private void setChartCount(int count) {
        objectMapper.writeValue(this.getStore(), new ChartCount(count));
    }

    @SneakyThrows
    public void increment() {
        int incremented = this.getChartCount() + 1;
        this.setChartCount(incremented);
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartCount {
        private int chartsGenerated;
    }


}
