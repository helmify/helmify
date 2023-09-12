package com.start.helm.domain.ui;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for index page.
 * <p>
 * Users visiting the application will talk to this controller first.
 */
@Controller
public class IndexController {

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("message", "hello world");
    return "index";
  }

  @GetMapping("/about")
  public String about() {
    return "about";
  }

}
