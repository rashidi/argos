package posmy.argos.security;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rashidi Zin
 */
class SecurityHelperTests {

    @BeforeAll
    static void registerUserContext() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("name", "rashidi").build();
        var authentication = new PreAuthenticatedAuthenticationToken(new UserPrincipal(null, claims), "", List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Get current logged in username")
    void getLoggedInUsername() {
        assertThat(SecurityHelper.getLoggedInUsername()).isEqualTo("rashidi");
    }

}
