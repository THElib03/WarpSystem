package eu.buildsouthwest.warp.core.search;

import java.util.Set;
import java.util.UUID;

/**
 * Immutable Record of all the Information used by the search engine to index a warp
 *
 * @param warpId
 * @param displayName
 * @param countryCode
 * @param region
 * @param city
 * @param type
 * @param tags
 * @param isAlias
 */
public record IndexWarp(
        UUID warpId,
        String displayName,
        String countryCode,
        String region,
        String city,
        String type,
        Set<String> tags,
        boolean isAlias
) {}