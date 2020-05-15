package posmy.argos.desk.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.rest.core.ValidationErrors;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;

import static org.assertj.core.api.Assertions.assertThat;
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

        verify(repository).exists(criteriaCaptor.capture());
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());

        var probedDesk = criteriaCaptor.getValue().getProbe();

        assertThat(probedDesk)
                .extracting(Desk::location, Desk::status)
                .containsOnly(desk.location(), OCCUPIED);
    }

    @Test
    @DisplayName("OCCUPIED Desk cannot be selected")
    void validateWithOccupiedDesk() {
        doReturn(true).when(repository).exists(any());

        var desk = create().status(OCCUPIED);
        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        ArgumentCaptor<String> errorsCaptor = forClass(String.class);

        verify(repository).exists(criteriaCaptor.capture());
        verify(errors).rejectValue(errorsCaptor.capture(), errorsCaptor.capture(), errorsCaptor.capture());

        var probedDesk = criteriaCaptor.getValue().getProbe();

        assertThat(probedDesk)
                .extracting(Desk::location, Desk::status)
                .containsOnly(desk.location(), OCCUPIED);

        var errorContent = errorsCaptor.getAllValues();

        assertThat(errorContent)
                .containsOnly("location", "location.occupied", "desk.location is not available");
    }

}
