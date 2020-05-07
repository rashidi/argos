package posmy.argos.security;

import com.microsoft.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;
import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipalManager;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Rashidi Zin
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
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

        var response = restTemplate.exchange("/", GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    @DisplayName("Non actuator paths requires authentication")
    void accessWithoutAuthentication() {
        var response = restTemplate.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @TestConfiguration
    static class UserPrincipalManagerTestConfiguration {

        @Bean
        @Primary
        public AADAppRoleStatelessAuthenticationFilter aadAppRoleStatelessAuthenticationFilter() throws ParseException, JOSEException, BadJOSEException {
            var principalManager = mock(UserPrincipalManager.class);

            var roles = new JSONArray().appendElement("USER");
            var claims = new JWTClaimsSet.Builder().claim("roles", roles).build();

            var principal = new UserPrincipal(null, claims);

            doReturn(principal).when(principalManager).buildUserPrincipal(anyString());

            return new AADAppRoleStatelessAuthenticationFilter(principalManager);
        }

    }

}
