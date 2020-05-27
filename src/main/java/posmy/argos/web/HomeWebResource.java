package posmy.argos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Rashidi Zin
 */
@Controller
public class HomeWebResource {
    
    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
}