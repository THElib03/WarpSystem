package eu.buildsouthwest.warp.core.search;

import java.util.function.Predicate;

/**
 * Record Used to filter out the Index before searching
 *
 * @param targetCountry
 * @param targetCategory
 */
public record SearchFilter(
        String targetCountry,
        String targetCategory
) implements Predicate<IndexWarp> {

    @Override
    public boolean test(IndexWarp entry) {
        if (targetCountry != null && !targetCountry.equalsIgnoreCase(entry.countryCode())) return false;
        if (targetCategory != null && !entry.tags().contains(targetCategory.toLowerCase())) return false;
        return true;
    }
}