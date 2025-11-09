import java.util.HashMap;
import java.util.Map;

public class CumulativeDistributionDemo {
    public static void main(String[] args) {
        // Example 1: UK Cities
        Map<String, Integer> ukCities = new HashMap<>();
        ukCities.put("London", 350);
        ukCities.put("Birmingham", 200);
        ukCities.put("Manchester", 150);
        ukCities.put("Glasgow", 100);
        ukCities.put("Other", 200);
        
        CumulativeDistribution<String> cityDistribution = new CumulativeDistribution<>(ukCities);
        cityDistribution.printDistribution();
        
        // Generate samples
        System.out.println("\n=== GENERATING 10 SAMPLES ===");
        for (int i = 0; i < 10; i++) {
            String city = cityDistribution.getRandomItem();
            System.out.printf("Sample %d: %s%n", i + 1, city);
        }
        
        // Test accuracy
        System.out.println("\n=== ACCURACY TEST (100,000 samples) ===");
        Map<String, Integer> results = cityDistribution.getDistributionSample(100000);
        
        results.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                double percentage = (entry.getValue() * 100.0) / 100000;
                System.out.printf("%-12s: %5d (%.2f%%)%n", 
                    entry.getKey(), entry.getValue(), percentage);
            });
        
        // Example 2: Product categories
        System.out.println("\n\n=== PRODUCT CATEGORIES EXAMPLE ===");
        Map<String, Integer> products = Map.of(
            "Electronics", 40,
            "Clothing", 30,
            "Books", 20,
            "Home", 10
        );

        CumulativeDistribution<String> productDistribution = new CumulativeDistribution<>(products);
        productDistribution.printDistribution();
    }
}