package posmy.argos.desk.event.handler;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.desk.helper.DeskTestHelper.create;

/**
 * @author Rashidi Zin
 */
class DeskBeforeSaveEventHandlerTests {

    private final DeskBeforeSaveEventHandler handler = new DeskBeforeSaveEventHandler();

    @BeforeAll
    static void registerUserContext() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("name", "rashidi").build();
        var authentication = new PreAuthenticatedAuthenticationToken(new UserPrincipal(null, claims), "", List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Current logged in user will be assigned as occupant")
    void assignOccupant() {
        var desk = create().status(OCCUPIED);

        handler.assignOccupant(desk);

        assertThat(desk.occupant()).isEqualTo("rashidi");
    }

    @Test
    @DisplayName("Occupant remains when status is VACANT")
    void assignOccupantWithStatusVacant() {
        var desk = create().status(VACANT).occupant("rashidi");

        handler.assignOccupant(desk);

        assertThat(desk.occupant()).isEqualTo(desk.occupant());
    }

    @Test
    @DisplayName("Occupant will be removed when status is VACANT")
    void removeOccupant() {
        var desk = create().status(VACANT).occupant("rashidi");

        handler.removeOccupant(desk);

        assertThat(desk.occupant()).isNull();

    }

    @Test
    @DisplayName("Occupant will remain when status is OCCUPIED")
    void removeOccupantWithStatusOccupied() {
        var desk = create().status(OCCUPIED).occupant("rashidi");

        handler.removeOccupant(desk);

        assertThat(desk.occupant()).isEqualTo(desk.occupant());

    }

}
