package com.start.helm.domain;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.HelmChartService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DownloadController {

  private final HelmChartService helmChartService;

  private Map<String, ByteArrayResource> cache = new HashMap<>();

  @Getter
  @Setter
  public static class DownloadRequest {
    private HelmContext helmContext;
  }

  @PostMapping(path = "/download/{name}")
  public ResponseEntity<?> download(@RequestBody DownloadRequest request, Model model, @PathVariable("name") String name) {

    HelmContext helmContext = request.getHelmContext();
    byte[] byteArray = helmChartService.process(helmContext);
    ByteArrayResource resource = new ByteArrayResource(byteArray);
    String uuid = UUID.randomUUID() + "-" + name;
    cache.put(uuid, resource);
    return ResponseEntity.ok().header("HX-Redirect", "/download/execute?key=" + uuid).build();
  }

  @GetMapping(path = "/download/execute")
  public ResponseEntity<Resource> download(@RequestParam("key") String key) {
    ByteArrayResource resource = cache.remove(key);
    return ResponseEntity.ok().headers(this.headers("helm.zip"))
        .contentLength(resource.contentLength())
        .contentType(MediaType.parseMediaType
            ("application/octet-stream")).body(resource);
  }


  private HttpHeaders headers(String name) {

    HttpHeaders header = new HttpHeaders();
    header.add(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + name);
    header.add("Cache-Control", "no-cache, no-store,"
        + " must-revalidate");
    header.add("Pragma", "no-cache");
    header.add("Expires", "0");
    return header;

  }
}
