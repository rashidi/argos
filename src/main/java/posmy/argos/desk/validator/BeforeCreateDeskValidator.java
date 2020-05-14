package posmy.argos.desk.validator;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;

import java.util.Objects;

/**
 * @author Rashidi Zin
 */
@AllArgsConstructor
public class BeforeCreateDeskValidator implements Validator {

    private final DeskRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Desk.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var source = (Desk) target;

        assertRequiredFields(source, errors);
        assertIfExist(source, errors);
    }

    void assertIfExist(Desk desk, Errors errors) {
        var findByLocation = Example.of(new Desk().location(desk.location()));

        boolean isExist = repository.exists(findByLocation);

        if (isExist) {
            errors.rejectValue("location", "location.exist", "desk.location already exist");
        }
    }

    void assertRequiredFields(Desk desk, Errors errors) {
        var location = desk.location();

        if (Objects.isNull(location) || StringUtils.isEmpty(location.row()) || Objects.isNull(location.column())) {
            errors.rejectValue("location", "location.missing", "desk.location is required");
        }

        var area = desk.area();

        if (Objects.isNull(area)) {
            errors.rejectValue("area", "area.missing", "desk.area is required");
        }

    }

}
