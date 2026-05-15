package com.wordy.server.model.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class WordDictionary {

    private final Set<String> words;

    public WordDictionary(Set<String> words) {
        this.words = Set.copyOf(words);
    }

    public static WordDictionary loadDefault() throws IOException {
        Path projectFile = Path.of("words.txt");
        if (Files.isRegularFile(projectFile)) {
            return loadFromPath(projectFile);
        }

        InputStream stream = WordDictionary.class.getClassLoader().getResourceAsStream("words.txt");
        if (stream != null) {
            try (stream) {
                return loadFromStream(stream);
            }
        }

        throw new IOException("words.txt not found on classpath or in working directory");
    }

    public static WordDictionary loadFromPath(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return loadFromReader(reader);
        }
    }

    public static WordDictionary loadFromStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return loadFromReader(reader);
        }
    }

    private static WordDictionary loadFromReader(BufferedReader reader) throws IOException {
        Set<String> dictionary = new HashSet<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String normalized = normalize(line);
            if (!normalized.isEmpty()) {
                dictionary.add(normalized);
            }
        }
        return new WordDictionary(dictionary);
    }

    public ValidationResult validate(String word, List<String> availableLetters) {
        if (word == null || word.isBlank()) {
            return ValidationResult.invalid("Word cannot be empty");
        }

        String normalized = normalize(word);
        if (!words.contains(normalized)) {
            return ValidationResult.invalid("Word is not in the dictionary");
        }

        if (!canFormFromLetters(normalized, availableLetters)) {
            return ValidationResult.invalid("Word cannot be formed from the available letters");
        }

        return ValidationResult.valid(normalized);
    }

    public boolean canFormFromLetters(String word, List<String> availableLetters) {
        Map<Character, Integer> available = new HashMap<>();
        for (String letter : availableLetters) {
            if (letter == null || letter.isEmpty()) {
                continue;
            }
            char c = Character.toUpperCase(letter.charAt(0));
            available.merge(c, 1, Integer::sum);
        }

        for (int i = 0; i < word.length(); i++) {
            char c = Character.toUpperCase(word.charAt(i));
            Integer remaining = available.get(c);
            if (remaining == null || remaining <= 0) {
                return false;
            }
            available.put(c, remaining - 1);
        }
        return true;
    }

    private static String normalize(String word) {
        return word.trim().toLowerCase(Locale.ROOT);
    }

    public record ValidationResult(boolean valid, String normalizedWord, String message) {
        public static ValidationResult valid(String normalizedWord) {
            return new ValidationResult(true, normalizedWord, "Accepted");
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, "", message);
        }
    }
}
