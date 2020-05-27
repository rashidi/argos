package posmy.argos.security.azure;

import java.text.ParseException;

import com.microsoft.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;
import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipalManager;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import net.minidev.json.JSONArray;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author Rashidi Zin
 */
@TestConfiguration
public class AzureADTestConfiguration {

    @Bean
    @Primary
    public AADAppRoleStatelessAuthenticationFilter aadAppRoleStatelessAuthenticationFilter() throws ParseException, JOSEException, BadJOSEException {
        final var principalManager = mock(UserPrincipalManager.class);

        final var roles = new JSONArray().appendElement("USER");
        final var claims = new JWTClaimsSet.Builder().claim("roles", roles).build();

        final var principal = new UserPrincipal(null, claims);

        doReturn(principal).when(principalManager).buildUserPrincipal(anyString());

        return new AADAppRoleStatelessAuthenticationFilter(principalManager);
    }

}