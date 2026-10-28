package uk.co.yottr.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcceptablePasswordValidatorTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private record Form(@AcceptablePassword String password) {
    }

    @Test
    public void aLongEnoughUncommonPasswordIsAccepted() {
        assertEquals(List.of(), problems("tidal-harbour-kettle"));
        assertEquals(List.of(), problems(null), "null is left to @NotNull");
    }

    @Test
    public void itMustBeAtLeastTwelveCharacters() {
        assertEquals(List.of("must be at least 12 characters"), problems("eleven-char"));
        assertEquals(List.of(), problems("twelve-chars"));
    }

    @Test
    public void itMustFitInTheSeventyTwoBytesBcryptUses() {
        assertEquals(List.of(), problems("é".repeat(36)), "72 bytes");
        assertEquals(List.of("is too long"), problems("é".repeat(37)), "74 bytes, though only 37 characters");
    }

    @Test
    public void commonPasswordsAreRefusedWhateverTheirCase() {
        final List<String> common = List.of("is one of the most common passwords, so it would be easy to guess");
        assertEquals(common, problems("password1234"));
        assertEquals(common, problems("Password1234"));
        assertEquals(common, problems("QWERTYUIOP12"));
    }

    private static List<String> problems(String password) {
        final Set<ConstraintViolation<Form>> violations = VALIDATOR.validate(new Form(password));
        return violations.stream().map(ConstraintViolation::getMessage).toList();
    }
}
