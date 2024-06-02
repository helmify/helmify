package me.helmify.domain.ui.counter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.helmify.app.FileStoreService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChartCounter {

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
