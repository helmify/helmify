package com.start.helm.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Component executing logic on startup to check if we can read / write to the data directory.
 */
@Slf4j
@Component
public class StartupCheck {

    @Value("${helm-start.data-directory:helm-start-data}")
    private String dataDirectory;

    @EventListener
    public void onStartup(ApplicationReadyEvent evt) {
        try {
            read();
            log.info("Read Check on {} OK", dataDirectory);
            write();
            log.info("Write Check on {} OK", dataDirectory);
        } catch (Exception e) {
            log.error("Error on startup", e);
        }
    }

    private void write() throws Exception {
        Files.createDirectory(Paths.get(dataDirectory, "startup-check"));
        Files.write(Paths.get(dataDirectory, "startup-check", System.currentTimeMillis() + ".txt"), "OK".getBytes());
    }

    private void read() throws Exception {
        try (Stream<Path> paths = Files.list(Paths.get(dataDirectory))) {
            log.info("Found {} paths in {}", paths.toList().size(), dataDirectory);
        }
    }

}
