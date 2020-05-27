package posmy.argos.security;

import posmy.argos.security.azure.AzureADTestConfiguration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Rashidi Zin
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = OAuthSecurityConfiguration.class)
@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@Import(AzureADTestConfiguration.class)
class OAuthSecurityConfigurationTests {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Actuator endpoints can be accessed without authentication")
    void actuator() {
        var response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    @DisplayName("Providing JWT will allow users to access non actuator paths")
    void accessWithAuthentication() {
        var headers = new HttpHeaders();

        headers.add(AUTHORIZATION, "Bearer eyThI5isnOtaR3aLt0k3nUcANtrY2c0pYi7bu7ItwOn7W0rK");

        var response = restTemplate.exchange("/api", GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    @DisplayName("Non actuator paths requires authentication")
    void accessWithoutAuthentication() {
        var response = restTemplate.getForEntity("/api/desks", String.class);

        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

}
