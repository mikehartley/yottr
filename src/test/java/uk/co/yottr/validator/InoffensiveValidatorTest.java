package uk.co.yottr.validator;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InoffensiveValidatorTest {

    @Test
    public void aListedWordIsFoundWhateverItsCase() {
        assertEquals(Optional.of("bollocks"), InoffensiveValidator.offensiveWordIn("What a load of BOLLOCKS."));
    }

    @Test
    public void aListedWordIsFoundWithAPlainEnding() {
        assertEquals(Optional.of("fuck"), InoffensiveValidator.offensiveWordIn("No fuckers"));
        assertEquals(Optional.of("wank"), InoffensiveValidator.offensiveWordIn("wanking"));
        assertEquals(Optional.of("tosser"), InoffensiveValidator.offensiveWordIn("tossers"));
    }

    @Test
    public void wordsAreSeparatedByAnythingButLetters() {
        assertEquals(Optional.of("twat"), InoffensiveValidator.offensiveWordIn("big_twat99"));
    }

    @Test
    public void onlyWholeWordsMatch() {
        assertEquals(Optional.empty(), InoffensiveValidator.offensiveWordIn("Sailing from Scunthorpe, Penistone or Cockermouth"));
        assertEquals(Optional.empty(), InoffensiveValidator.offensiveWordIn("Bring your own lifejacket; no smoking below."));
    }

    @Test
    public void wordsWithAnInnocentMeaningAboardAreLeftAlone() {
        assertEquals(Optional.empty(), InoffensiveValidator.offensiveWordIn(
                "Prick off a position in the cockpit by a chink of light, then have a fag with the cocker spaniel"));
    }

    @Test
    public void nothingIsFineToo() {
        assertEquals(Optional.empty(), InoffensiveValidator.offensiveWordIn(null));
        assertEquals(Optional.empty(), InoffensiveValidator.offensiveWordIn("  "));
    }
}
