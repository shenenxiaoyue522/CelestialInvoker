package com.xiaoyue.celestial_invoker.simple;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCaser {

    public static String toCamelCase(String input) {
        Pattern pattern = Pattern.compile("[\\s']+");
        Matcher matcher = pattern.matcher(input);
        String[] words = pattern.split(matcher.replaceAll(" ").toLowerCase().trim());
        StringBuilder camelCaseOutput = new StringBuilder(words[0]);

        for(int i = 1; i < words.length; ++i) {
            camelCaseOutput.append(toProperCase(words[i]));
        }

        return camelCaseOutput.toString();
    }

    private static String toProperCase(String s) {
        String var10000 = s.substring(0, 1).toUpperCase();
        return var10000 + s.substring(1);
    }

    public static String caseSpaceCapitalize(String id) {
        String input = id;
        if (id.contains("_")) {
            input = id.replace('_', ' ');
        }

        if (input.contains(".")) {
            input = input.replace('.', ' ');
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split(" ");

        for(String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1).toLowerCase());
            result.append(" ");
        }

        return result.toString().trim();
    }

    public static String allLowerCaseFirst(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("\\.")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}
