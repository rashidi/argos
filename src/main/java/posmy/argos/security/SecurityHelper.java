package posmy.argos.security;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Rashidi Zin
 */
@NoArgsConstructor(access = PRIVATE)
public final class SecurityHelper {

    public static String getLoggedInUsername() {
        var principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal.getName();
    }

}
