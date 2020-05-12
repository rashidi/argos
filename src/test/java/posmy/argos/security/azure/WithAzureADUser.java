package posmy.argos.security.azure;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Rashidi Zin
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAzureADUserSecurityContextFactory.class)
public @interface WithAzureADUser {

    String name() default "rashidi";

    String[] roles() default { "ROLE_USER" };

}
