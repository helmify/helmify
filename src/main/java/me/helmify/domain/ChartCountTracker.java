package me.helmify.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.events.ChartDownloadedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChartCountTracker {

	private final FileStoreService fileStoreService;

	private final ObjectMapper objectMapper;

	@SneakyThrows
	public int getChartCount() {
		File store = this.fileStoreService.getStore();
		if (store == null)
			return -1;
		return Math.max(0, this.objectMapper.readValue(store, ChartCount.class).getChartsGenerated());
	}

	@SneakyThrows
	private void setChartCount(int count) {
		File store = this.fileStoreService.getStore();
		if (store != null) {
			objectMapper.writeValue(store, new ChartCount(count));
		}
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
