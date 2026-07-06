package eu.buildsouthwest.warp.core.search;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MultiDimensionalSearchEngine {

    public static class TrieNode {
        final Map<Character, TrieNode> children = new ConcurrentHashMap<>();
        // Use CopyOnWriteArrayList if we notice slow behavior / high ram usage, tho lag for write
        final Set<IndexWarp> associations = ConcurrentHashMap.newKeySet();
    }

    @Getter
    private final TrieNode root = new TrieNode();

    public void insertToken(String token, IndexWarp entry) {
        TrieNode current = root;
        for (char ch : token.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.associations.add(entry);
    }

    public void indexWarp(UUID id, String displayName, String country, String region, String city, String type, Set<String> tags) {
        IndexWarp primaryEntry = new IndexWarp(id, displayName, country, region, city, type, tags, false);

        insertToken(displayName.toLowerCase(), primaryEntry);
        if (city != null) insertToken(city.toLowerCase(), primaryEntry);
        if (region != null) insertToken(region.toLowerCase(), primaryEntry);
        if (country != null) insertToken(country.toLowerCase(), primaryEntry);
        for (String tag : tags) {
            insertToken(tag.toLowerCase(), primaryEntry);
        }
    }

    private void collectRecursive(TrieNode node, SearchFilter filter, List<IndexWarp> results, int limit) {
        if (results.size() >= limit) return;

        // Apply dynamic predicate filtering on traversal
        for (IndexWarp entry : node.associations) {
            if (results.size() >= limit) return;
            if (filter.test(entry) && !results.contains(entry)) {
                results.add(entry);
            }
        }

        for (TrieNode child : node.children.values()) {
            collectRecursive(child, filter, results, limit);
        }
    }

    public List<IndexWarp> search(String prefix, SearchFilter filter, int limit) {
        // Preallocated for speed I guess
        List<IndexWarp> results = new ArrayList<>(limit);
        TrieNode current = root;

        for (char ch : prefix.toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return results;
        }

        // Deep collect matches starting from this prefix junction
        collectRecursive(current, filter, results, limit);
        return results;
    }


}