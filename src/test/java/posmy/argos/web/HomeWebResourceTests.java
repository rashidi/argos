package posmy.argos.web;

import posmy.argos.security.azure.AzureADTestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(controllers = HomeWebResource.class)
@Import(AzureADTestConfiguration.class)
public class HomeWebResourceTests {
    
    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = webAppContextSetup(ctx)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("No authentication needed for index page")
    void index() throws Exception {
        mvc.perform(get("/"))
            .andExpect(status().isOk());
    }
    
}