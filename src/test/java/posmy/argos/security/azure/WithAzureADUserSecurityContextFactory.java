package posmy.argos.security.azure;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * @author Rashidi Zin
 */
public class WithAzureADUserSecurityContextFactory implements WithSecurityContextFactory<WithAzureADUser> {

    @Override
    public SecurityContext createSecurityContext(WithAzureADUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        var name = annotation.name();
        var roles = Stream.of(annotation.roles()).map(SimpleGrantedAuthority::new).collect(toUnmodifiableList());

        JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("name", name).build();

        var authentication = new PreAuthenticatedAuthenticationToken(new UserPrincipal(null, claims), "", roles);

        context.setAuthentication(authentication);

        return context;
    }

}
