package com.start.helm.cli.config;

import com.start.helm.cli.events.HelmZipDownloadedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AppConfigListener {

    private final RestTemplate restTemplate;
    private final ApplicationContext context;

    public AppConfigListener(RestTemplate restTemplate, ApplicationContext context) {
        this.restTemplate = restTemplate;
        this.context = context;
    }

    @EventListener
    public void listen(AppConfig config) {
        String buildFile = config.getBuildFile();
        try {
            Path buildFilePath = Path.of(buildFile);
            String buildFileContents = Files.readString(buildFilePath);
            String url = String.format("http://localhost:8080/api/cli?name=%s&version=%s", config.getAppName(), config.getAppVersion());
            System.out.println("sending request to " + url);
            ResponseEntity<byte[]> binary = restTemplate.postForEntity(url, buildFileContents, byte[].class);

            if (!binary.getStatusCode().is2xxSuccessful()) {
                System.err.println("error sending buildfile to server");
                return;
            }
            String helmZipPath = Paths.get(buildFilePath.getParent().toFile().getAbsolutePath(), "helm.zip").toFile().getAbsolutePath();
            context.publishEvent(new HelmZipDownloadedEvent(binary.getBody(), helmZipPath));

        } catch (Exception e) {
            System.err.println("error reading buildfile at " + config.getBuildFile());
            e.printStackTrace();
        }

    }

}
