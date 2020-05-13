package posmy.argos.desk.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
class BeforeCreateDeskValidatorTests {

    private final DeskRepository repository = mock(DeskRepository.class);

    private final BeforeCreateDeskValidator validator = new BeforeCreateDeskValidator(repository);

    @Test
    @DisplayName("Only Desk class is supported")
    void supports() {
        assertThat(validator.supports(Desk.class)).isTrue();

        assertThat(validator.supports(String.class)).isFalse();
    }

    @Test
    void validate() {
    }
}
