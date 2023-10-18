package com.start.helm.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.helm.domain.events.ChartDownloadedEvent;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class ChartCountTracker {

    private final ObjectMapper objectMapper;

    @Value("${helm-start.data-directory:helm-start-data}")
    private String dataDirectory;

    @SneakyThrows
    private File getStore() {
        Path dataDirectory = Paths.get(this.dataDirectory);
        if (!Files.exists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }

        Path filePath = Paths.get(dataDirectory.toFile().getAbsolutePath(), "chart-count.json");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            String json = this.objectMapper.writeValueAsString(new ChartCount(0));
            Files.write(filePath, json.getBytes());
        }

        return filePath.toFile();
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
    private void increment() {
        int incremented = this.getChartCount() + 1;
        this.setChartCount(incremented);
    }

    @EventListener
    public void onIncrementEvent(ChartDownloadedEvent evt) {
        this.increment();
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartCount {
        private int chartsGenerated;
    }


}
