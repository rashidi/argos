package posmy.argos.desk.validator;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.rest.core.ValidationErrors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.desk.helper.DeskTestHelper.create;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
class BeforeSaveDeskValidatorTests {

    private final DeskRepository repository = mock(DeskRepository.class);

    private final BeforeSaveDeskValidator validator = new BeforeSaveDeskValidator(repository);

    @Captor
    private ArgumentCaptor<Example<Desk>> criteriaCaptor;

    @BeforeAll
    static void registerUserContext() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("name", "rashidi").build();
        var authentication = new PreAuthenticatedAuthenticationToken(new UserPrincipal(null, claims), "", List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Supports only Desk")
    void supports() {
        assertThat(validator.supports(Desk.class)).isTrue();

        assertThat(validator.supports(String.class)).isFalse();
    }

    @Test
    @DisplayName("Desk with status VACANT will not be validated")
    void validateWithStatusVacant() {
        var desk = create().status(VACANT);
        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        verify(repository, never()).exists(any());
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("No errors will be thrown if Desk is VACANT")
    void validateWithVacantDesk() {
        doReturn(false).when(repository).exists(any());

        var desk = create().status(OCCUPIED);
        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        verify(repository, times(2)).exists(criteriaCaptor.capture());
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());

        assertThat(criteriaCaptor.getAllValues())
                .hasSize(2)
                .extracting(Example::getProbe)
                .satisfies(probe ->
                        assertThat(probe)
                                .extracting(Desk::occupant, Desk::status)
                                .containsOnly("rashidi", OCCUPIED),
                        atIndex(0)
                )
                .satisfies(probe ->
                        assertThat(probe)
                                .extracting(Desk::location, Desk::status)
                                .containsOnly(desk.location(), OCCUPIED),
                        atIndex(1)
                );
    }

    @Test
    @DisplayName("OCCUPIED Desk cannot be selected")
    void validateWithOccupiedDesk() {
        var occupantProbe = Example.of(new Desk().occupant("rashidi").status(OCCUPIED));

        doReturn(false).when(repository).exists(eq(occupantProbe));

        var locationProbe = Example.of(new Desk().location(create().location()).status(OCCUPIED));

        doReturn(true).when(repository).exists(eq(locationProbe));

        var desk = create().status(OCCUPIED);
        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        ArgumentCaptor<String> errorsCaptor = forClass(String.class);

        verify(repository, times(2)).exists(criteriaCaptor.capture());
        verify(errors).rejectValue(errorsCaptor.capture(), errorsCaptor.capture(), errorsCaptor.capture());

        var probedDesk = criteriaCaptor.getValue().getProbe();

        assertThat(probedDesk)
                .extracting(Desk::location, Desk::status)
                .containsOnly(desk.location(), OCCUPIED);

        var errorContent = errorsCaptor.getAllValues();

        assertThat(errorContent)
                .containsOnly("location", "location.occupied", "desk.location is not available");
    }

    @Test
    @DisplayName("Active occupant cannot occupy another desk")
    void validateWithActiveOccupant() {

        var occupantProbe = Example.of(new Desk().occupant("rashidi").status(OCCUPIED));

        doReturn(true).when(repository).exists(eq(occupantProbe));

        var locationProbe = Example.of(new Desk().location(create().location()).status(OCCUPIED));

        doReturn(false).when(repository).exists(eq(locationProbe));

        var desk = create().status(OCCUPIED);
        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        ArgumentCaptor<String> errorsCaptor = forClass(String.class);

        verify(repository, times(2)).exists(criteriaCaptor.capture());
        verify(errors).rejectValue(errorsCaptor.capture(), errorsCaptor.capture(), errorsCaptor.capture());

        assertThat(criteriaCaptor.getAllValues())
                .first()
                .extracting(Example::getProbe)
                .extracting(Desk::occupant, Desk::status)
                .containsOnly("rashidi", OCCUPIED);

        var errorContent = errorsCaptor.getAllValues();

        assertThat(errorContent)
                .contains("occupant", "occupant.active", "desk.occupant is active");
    }

    @Test
    @DisplayName("No errors will be thrown for inactive occupant")
    void validateWithInactiveOccupant() {
        doReturn(false).when(repository).exists(any());

        var desk = create().status(OCCUPIED);
        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        ArgumentCaptor<String> errorsCaptor = forClass(String.class);

        verify(repository, times(2)).exists(criteriaCaptor.capture());
        verify(errors, never()).rejectValue(errorsCaptor.capture(), errorsCaptor.capture(), errorsCaptor.capture());
    }
}
