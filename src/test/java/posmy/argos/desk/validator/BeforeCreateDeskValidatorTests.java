package posmy.argos.desk.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.rest.core.ValidationErrors;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;
import posmy.argos.desk.domain.DeskRepository;
import posmy.argos.desk.junit.arguments.InvalidLocationsArgumentProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
class BeforeCreateDeskValidatorTests {

    private final DeskRepository repository = mock(DeskRepository.class);

    private final BeforeCreateDeskValidator validator = new BeforeCreateDeskValidator(repository);

    @Captor
    private ArgumentCaptor<Example<Desk>> findCriteriaCaptor;

    @Captor
    private ArgumentCaptor<String> errorMessageCaptor;

    @Test
    @DisplayName("Only Desk class is supported")
    void supports() {
        assertThat(validator.supports(Desk.class)).isTrue();

        assertThat(validator.supports(String.class)).isFalse();
    }

    @Test
    @DisplayName("Providing an existing location will cause an error")
    void validateWithExistingLocation() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY);

        doReturn(true).when(repository).exists(any());

        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        verify(repository).exists(findCriteriaCaptor.capture());

        var locationCriteria = findCriteriaCaptor.getValue().getProbe().location();

        assertThat(locationCriteria).isEqualTo(location);

        verify(errors).rejectValue(anyString(), anyString(), errorMessageCaptor.capture());

        var errorMessage = errorMessageCaptor.getValue();

        assertThat(errorMessage).isEqualTo("desk.location already exist");
    }

    @Test
    @DisplayName("No errors should be assigned for new location")
    void validateWithNewLocation() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY);

        doReturn(false).when(repository).exists(any());

        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        verify(repository).exists(findCriteriaCaptor.capture());

        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidLocationsArgumentProvider.class)
    @DisplayName("location and its content are required")
    void invalidLocations() {
        var desk = new Desk().area(DATA_AND_TECHNOLOGY);

        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        ArgumentCaptor<String> errorsCaptor = ArgumentCaptor.forClass(String.class);

        verify(errors).rejectValue(errorsCaptor.capture(), errorsCaptor.capture(), errorsCaptor.capture());

        var errorsContent = errorsCaptor.getAllValues();

        assertThat(errorsContent).contains("location", "location.missing", "desk.location is required");
    }

    @Test
    @DisplayName("area cannot be null")
    void areaIsNull() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location);

        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        ArgumentCaptor<String> errorsCaptor = ArgumentCaptor.forClass(String.class);

        verify(errors).rejectValue(errorsCaptor.capture(), errorsCaptor.capture(), errorsCaptor.capture());

        var errorsContent = errorsCaptor.getAllValues();

        assertThat(errorsContent).contains("area", "area.missing", "desk.area is required");
    }

}
