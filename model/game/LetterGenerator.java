package com.wordy.server.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class LetterGenerator {

    private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U'};
    private static final char[] CONSONANTS = {
            'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final int LETTER_COUNT = 20;
    private static final int MIN_VOWELS = 5;
    private static final int MAX_VOWELS = 7;

    private final Random random;

    public LetterGenerator() {
        this(new Random());
    }

    public LetterGenerator(Random random) {
        this.random = random;
    }

    public List<String> generate() {
        int vowelCount = MIN_VOWELS + random.nextInt(MAX_VOWELS - MIN_VOWELS + 1);
        int consonantCount = LETTER_COUNT - vowelCount;

        List<Character> letters = new ArrayList<>(LETTER_COUNT);
        for (int i = 0; i < vowelCount; i++) {
            letters.add(VOWELS[random.nextInt(VOWELS.length)]);
        }
        for (int i = 0; i < consonantCount; i++) {
            letters.add(CONSONANTS[random.nextInt(CONSONANTS.length)]);
        }

        Collections.shuffle(letters, random);

        List<String> result = new ArrayList<>(LETTER_COUNT);
        for (char letter : letters) {
            result.add(String.valueOf(letter));
        }
        return result;
    }
}
