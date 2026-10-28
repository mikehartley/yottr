package uk.co.yottr.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class AcceptablePasswordValidator implements ConstraintValidator<AcceptablePassword, String> {

    static final int MIN_LENGTH = 12;

    // bcrypt uses only the first 72 bytes, so anything after that would be ignored without the member knowing
    static final int MAX_BYTES = 72;

    /**
     * Common passwords of at least MIN_LENGTH characters, lower-cased: anything shorter is refused anyway. Taken
     * from the xato-net 1,000,000 and NCSC 100,000 lists in SecLists (MIT licence,
     * https://github.com/danielmiessler/SecLists, Passwords/Common-Credentials).
     */
    private static final String COMMON_PASSWORDS = "/common-passwords.txt";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext ctx) {
        if (password == null) {
            return true;
        }

        final String problem;
        if (password.length() < MIN_LENGTH) {
            problem = "must be at least " + MIN_LENGTH + " characters";
        }
        else if (password.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            problem = "is too long";
        }
        else if (CommonPasswords.SET.contains(password.toLowerCase(Locale.ROOT))) {
            problem = "is one of the most common passwords, so it would be easy to guess";
        }
        else {
            return true;
        }

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(problem).addConstraintViolation();
        return false;
    }

    /**
     * Loaded the first time a password is checked, and kept.
     */
    private static final class CommonPasswords {

        static final Set<String> SET = load();

        private static Set<String> load() {
            try (InputStream in = AcceptablePasswordValidator.class.getResourceAsStream(COMMON_PASSWORDS)) {
                if (in == null) {
                    throw new IllegalStateException(COMMON_PASSWORDS + " is missing");
                }
                return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                        .collect(Collectors.toUnmodifiableSet());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
