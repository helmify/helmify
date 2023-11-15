package com.start.helm.cli;

import com.start.helm.cli.events.HelmZipDownloadedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class HelmZipDownloadListener {

    @EventListener
    public void handleHelmZipDownloadedEvent(HelmZipDownloadedEvent event) {
        System.out.println("Helm zip downloaded to: " + event.getPath());
        try {
            Path helmZipPath = Paths.get(event.getPath());
            Files.write(helmZipPath, event.getZipFile());
            File helmDir = Paths.get(helmZipPath.getParent().toFile().getAbsolutePath(), "helm").toFile();
            ZipUtil.unpack(helmZipPath.toFile(), helmDir);
            System.out.println("Helm zip extracted to: " + helmDir.getAbsolutePath());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
