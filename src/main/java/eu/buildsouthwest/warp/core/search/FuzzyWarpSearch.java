package eu.buildsouthwest.warp.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FuzzyWarpSearch {

    public List<IndexWarp> searchFuzzy(MultiDimensionalSearchEngine engine, String input, int maxErrors, int limit) {
        // Pre-allocate for speed!
        List<IndexWarp> results = new ArrayList<>(limit);
        String query = input.toLowerCase();

        int[] initialRow = new int[query.length() + 1];
        for (int i = 0; i <= query.length(); i++) {
            initialRow[i] = i;
        }

        MultiDimensionalSearchEngine.TrieNode rootNode = engine.getRoot();
        for (Map.Entry<Character, MultiDimensionalSearchEngine.TrieNode> child : rootNode.children.entrySet()) {
            evaluateLevenshtein(
                    child.getValue(),
                    child.getKey(),
                    query,
                    initialRow,
                    maxErrors,
                    results,
                    limit
            );
        }

        return results;
    }

    public void evaluateLevenshtein(MultiDimensionalSearchEngine.TrieNode node, char character,
                                    String query, int[] parentRow, int maxErrors,
                                    List<IndexWarp> results, int limit) {

        int size = query.length();
        int[] currentRow = new int[size + 1];
        currentRow[0] = parentRow[0] + 1;

        int minimumElement = currentRow[0];

        // Fill out the Levenshtein row values for this node depth
        for (int i = 1; i <= size; i++) {
            int insertCost = currentRow[i - 1] + 1;
            int deleteCost = parentRow[i] + 1;
            int substituteCost = (query.charAt(i - 1) == character) ? parentRow[i - 1] : parentRow[i - 1] + 1;

            currentRow[i] = Math.min(Math.min(insertCost, deleteCost), substituteCost);
            minimumElement = Math.min(minimumElement, currentRow[i]);
        }

        // If the last cell's value falls within our error budget, we found a match token!
        if (currentRow[size] <= maxErrors && !node.associations.isEmpty()) {
            for (IndexWarp entry : node.associations) {
                if (results.size() < limit && !results.contains(entry)) {
                    results.add(entry);
                }
            }
        }

        // Pruning: If every path down this character branch exceeds our error allowance, drop it
        if (minimumElement <= maxErrors) {
            for (Map.Entry<Character, MultiDimensionalSearchEngine.TrieNode> child : node.children.entrySet()) {
                evaluateLevenshtein(child.getValue(), child.getKey(), query, currentRow, maxErrors, results, limit);
            }
        }
    }
}