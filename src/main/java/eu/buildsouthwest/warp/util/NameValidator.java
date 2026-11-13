package eu.buildsouthwest.warp.util;

import java.util.Optional;
import java.util.regex.Pattern;

public class NameValidator {
    private NameValidator() {}

    private static final Pattern VALID = Pattern.compile("^[a-zA-Z0-9_]+( [A-Za-z0-9_]+)*$");
    private static final int MAX_LENGTH = 60;

    public static Optional<String> validate(String raw){
        if(raw == null) return Optional.empty();

        String name = raw.trim();
        if(name.isEmpty() || name.length() > MAX_LENGTH) return Optional.empty();
        if(!VALID.matcher(name).matches()) return Optional.empty();

        return Optional.of(name);
    }
}
