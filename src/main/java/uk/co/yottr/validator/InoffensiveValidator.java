package uk.co.yottr.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InoffensiveValidator implements ConstraintValidator<Inoffensive, String> {

    private static final Set<String> WORDS = load();

    // anything but letters separates words, so big_twat and twat99 are caught
    private static final Pattern NOT_LETTERS = Pattern.compile("[^\\p{L}]+");

    // longest first, so that "fuckers" loses "ers" rather than "s"
    private static final List<String> ENDINGS = List.of("ing", "ers", "ed", "er", "es", "s");

    @Override
    public boolean isValid(String text, ConstraintValidatorContext ctx) {
        final Optional<String> word = offensiveWordIn(text);
        if (word.isPresent() && ctx != null) {
            // the word from the list, never the member's own text, goes into the message
            ctx.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("word", word.get());
        }
        return word.isEmpty();
    }

    /**
     * The first word in the text that's on the list, as the list has it.
     */
    static Optional<String> offensiveWordIn(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        for (String token : NOT_LETTERS.split(text.toLowerCase(Locale.UK))) {
            if (WORDS.contains(token)) {
                return Optional.of(token);
            }
            for (String ending : ENDINGS) {
                if (token.endsWith(ending) && WORDS.contains(token.substring(0, token.length() - ending.length()))) {
                    return Optional.of(token.substring(0, token.length() - ending.length()));
                }
            }
        }
        return Optional.empty();
    }

    private static Set<String> load() {
        try (var in = InoffensiveValidator.class.getResourceAsStream("/offensive-words.txt")) {
            if (in == null) {
                throw new IllegalStateException("offensive-words.txt is missing");
            }
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                    .map(String::strip)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(line -> line.toLowerCase(Locale.UK))
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
