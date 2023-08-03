package com.start.helm;

import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PomUploadController {


  @PostMapping("/upload-pom") public String uploadImage(Model model, @RequestParam("pom") MultipartFile mavenPom) throws IOException {
    int length = mavenPom.getBytes().length;
    model.addAttribute("message", "Uploaded pom.xml of length " + length + " bytes");
    return "fragments :: pom-upload-form";
  }

}
