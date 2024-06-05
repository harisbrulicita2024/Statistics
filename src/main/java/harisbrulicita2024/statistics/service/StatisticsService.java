package harisbrulicita2024.statistics.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> collectStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("service1", collectServiceStatistics("http://localhost:3050/api/jobs", "jobs"));
        statistics.put("service2", collectServiceStatistics("http://localhost:3050/api/users", "users"));
        statistics.put("service3", collectServiceStatistics("http://localhost:3050/api/tracking", "tracking"));
        return statistics;
    }

    private Map<String, Object> collectServiceStatistics(String url, String type) {
        try {
            Map<String, Object> stats = restTemplate.getForObject(url, Map.class);
            if (stats == null) {
                return new HashMap<>();
            }
            switch (type) {
                case "jobs":
                    return processJobStatistics(stats);
                case "users":
                    return processUserStatistics(stats);
                case "tracking":
                    return processTrackingStatistics(stats);
                default:
                    return stats;
            }
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private Map<String, Object> processJobStatistics(Map<String, Object> stats) {
        Map<String, Object> processedStats = new HashMap<>();
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) stats.get("jobs");

        if (jobs != null && !jobs.isEmpty()) {
            double averagePay = jobs.stream()
                    .mapToDouble(job -> ((Number) job.get("pay")).doubleValue())
                    .average()
                    .orElse(0.0);
            processedStats.put("averagePay", averagePay);
        } else {
            processedStats.put("averagePay", 0.0);
        }

        return processedStats;
    }

    private Map<String, Object> processUserStatistics(Map<String, Object> stats) {
        Map<String, Object> processedStats = new HashMap<>();
        List<Map<String, Object>> users = (List<Map<String, Object>>) stats.get("users");

        if (users != null && !users.isEmpty()) {
            double averageAge = users.stream()
                    .mapToInt(user -> Period.between(LocalDate.parse((String) user.get("birthdate")), LocalDate.now()).getYears())
                    .average()
                    .orElse(0.0);
            processedStats.put("averageAge", averageAge);
        } else {
            processedStats.put("averageAge", 0.0);
        }

        return processedStats;
    }

    private Map<String, Object> processTrackingStatistics(Map<String, Object> stats) {
        Map<String, Object> processedStats = new HashMap<>();
        List<Map<String, Object>> tracking = (List<Map<String, Object>>) stats.get("tracking");

        if (tracking != null && !tracking.isEmpty()) {
            long interviewCount = tracking.stream()
                    .filter(t -> (Boolean) t.get("interview"))
                    .count();
            processedStats.put("interviewCount", interviewCount);
        } else {
            processedStats.put("interviewCount", 0L);
        }

        return processedStats;
    }
}
