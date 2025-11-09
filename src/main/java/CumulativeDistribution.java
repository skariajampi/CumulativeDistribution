import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CumulativeDistribution<T> {
    private final List<DistributionEntry<T>> distribution;
    private final int totalWeight;
    
    private static class DistributionEntry<T> {
        final T item;
        final int weight;
        final double cumulativeProbability;
        
        DistributionEntry(T item, int weight, double cumulativeProbability) {
            this.item = item;
            this.weight = weight;
            this.cumulativeProbability = cumulativeProbability;
        }
    }
    
    public CumulativeDistribution(Map<T, Integer> itemsWithWeights) {
        this.distribution = new ArrayList<>();
        this.totalWeight = itemsWithWeights.values().stream().mapToInt(Integer::intValue).sum();
        
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight must be positive");
        }
        
        double cumulative = 0.0;
        for (Map.Entry<T, Integer> entry : itemsWithWeights.entrySet()) {
            double probability = (double) entry.getValue() / totalWeight;
            cumulative += probability;
            distribution.add(new DistributionEntry<>(
                entry.getKey(), entry.getValue(), cumulative
            ));
        }
        
        // Ensure last cumulative probability is exactly 1.0 (avoid floating point errors)
        if (!distribution.isEmpty()) {
            DistributionEntry<T> last = distribution.get(distribution.size() - 1);
            distribution.set(distribution.size() - 1, 
                new DistributionEntry<>(last.item, last.weight, 1.0));
        }
    }
    
    public T getRandomItem() {
        double randomValue = ThreadLocalRandom.current().nextDouble();
        return selectItem(randomValue);
    }
    
    private T selectItem(double randomValue) {
        for (DistributionEntry<T> entry : distribution) {
            if (randomValue <= entry.cumulativeProbability) {
                return entry.item;
            }
        }
        // Fallback to last item (should rarely happen)
        return distribution.get(distribution.size() - 1).item;
    }
    
    public void printDistribution() {
        System.out.println("=== CUMULATIVE DISTRIBUTION ===");
        System.out.printf("Total weight: %d%n", totalWeight);
        
        double previous = 0.0;
        for (DistributionEntry<T> entry : distribution) {
            double percentage = (entry.weight * 100.0) / totalWeight;
            System.out.printf("Range: %.3f - %.3f | Weight: %3d (%5.2f%%) → %s%n",
                previous, entry.cumulativeProbability, entry.weight, percentage, entry.item);
            previous = entry.cumulativeProbability;
        }
    }
    
    public Map<T, Integer> getDistributionSample(int sampleSize) {
        Map<T, Integer> results = new HashMap<>();
        for (int i = 0; i < sampleSize; i++) {
            T item = getRandomItem();
            results.put(item, results.getOrDefault(item, 0) + 1);
        }
        return results;
    }
    
    // Getters for inspection
    public List<DistributionEntry<T>> getDistribution() { return distribution; }
    public int getTotalWeight() { return totalWeight; }
}