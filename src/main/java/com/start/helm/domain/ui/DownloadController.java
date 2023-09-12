package com.start.helm.domain.ui;

import com.start.helm.domain.ChartCountTracker;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.HelmChartService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for File Download.
 * <p/>
 * This controller will be called last during a user's session. To support
 * triggering a download from an AJAX call, we offer two methods, the first
 * of which will cause HTMX to perform a redirect on the client side, pointing
 * to the second method which starts the actual download.
 */
@Controller
@RequiredArgsConstructor
public class DownloadController {

  private final HelmChartService helmChartService;
  private final ChartCountTracker chartCountTracker;

  private Map<String, ByteArrayResource> cache = new HashMap<>();

  @Getter
  @Setter
  public static class DownloadRequest {
    private HelmContext helmContext;
  }

  /**
   * Method for triggering a download.
   * This method triggers processing of the current {@link HelmContext} and caches
   * the resulting zip as a byte array in memory.
   */
  @PostMapping(path = "/download/{name}")
  public ResponseEntity<?> prepareDownload(@RequestBody DownloadRequest request, @PathVariable("name") String name) {
    HelmContext helmContext = request.getHelmContext();
    byte[] byteArray = helmChartService.process(helmContext);
    ByteArrayResource resource = new ByteArrayResource(byteArray);
    String uuid = UUID.randomUUID() + "-" + name;
    cache.put(uuid, resource);
    return ResponseEntity.ok().header("HX-Redirect", "/download/execute?key=" + uuid).build();
  }

  /**
   * Method for actually downloading a file. This method is called after a redirect
   * is performed on the client (issued by HTMX upon receiving HX-REDIRECT header in the response).
   */
  @GetMapping(path = "/download/execute")
  public ResponseEntity<Resource> download(@RequestParam("key") String key) {
    ByteArrayResource resource = cache.remove(key);
    chartCountTracker.increment();
    return ResponseEntity.ok().headers(this.headers("helm.zip")).contentLength(resource.contentLength())
        .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
  }


  /**
   * Util method for adding the headers necessary for a file download.
   */
  private HttpHeaders headers(String name) {
    HttpHeaders header = new HttpHeaders();
    header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
    header.add("Cache-Control", "no-cache, no-store, must-revalidate");
    header.add("Pragma", "no-cache");
    header.add("Expires", "0");
    return header;
  }
}
