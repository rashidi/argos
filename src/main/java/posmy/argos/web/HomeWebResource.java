package posmy.argos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Rashidi Zin
 */
@Controller
public class HomeWebResource {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
}