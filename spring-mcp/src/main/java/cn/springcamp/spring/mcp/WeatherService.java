package cn.springcamp.spring.mcp;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * Weather tools with mock data, so the demo runs without any external service.
 */
@Service
public class WeatherService {

    private static final String[] CONDITIONS = {"晴", "多云", "小雨", "小雪"};
    private static final String[] WIND_DIRECTIONS = {"东风", "南风", "西风", "北风"};
    private static final String[] ALERT_EVENTS = {"暴雨橙色预警", "高温黄色预警", "大风蓝色预警", "寒潮蓝色预警"};
    private static final String[] SEVERITIES = {"低", "中等", "高", "严重"};

    @Tool(description = "Get weather forecast for a specific latitude/longitude")
    public String getWeatherForecastByLocation(double latitude, double longitude) {
        StringBuilder forecast = new StringBuilder(String.format("坐标（%s, %s）未来三天预报：\n", latitude, longitude));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int day = 1; day <= 3; day++) {
            forecast.append(String.format("""
                    第%d天:
                    温度: %d°C
                    风力: %d级 %s
                    天气: %s
                    """, day, random.nextInt(-5, 36), random.nextInt(1, 9),
                    WIND_DIRECTIONS[random.nextInt(WIND_DIRECTIONS.length)],
                    CONDITIONS[random.nextInt(CONDITIONS.length)]));
        }
        return forecast.toString();
    }

    @Tool(description = "Get weather alerts for a US state. Input is Two-letter US state code (e.g. CA, NY)")
    public String getAlerts(String state) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String event = ALERT_EVENTS[random.nextInt(ALERT_EVENTS.length)];
        return String.format("""
                Event: %s
                Area: %s
                Severity: %s
                Description: %s 生效中，请注意防范。
                """, event, state, SEVERITIES[random.nextInt(SEVERITIES.length)], event);
    }
}
