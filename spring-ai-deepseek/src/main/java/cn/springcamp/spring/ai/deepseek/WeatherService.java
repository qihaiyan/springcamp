package cn.springcamp.spring.ai.deepseek;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WeatherService {

    private static final Map<Integer, String> CONDITIONS = Map.of(
            0, "晴", 1, "多云", 2, "小雨", 3, "小雪"
    );

    private final ToolCallRecorder recorder;

    public WeatherService(ToolCallRecorder recorder) {
        this.recorder = recorder;
    }

    @Tool(description = "查询指定城市的当前天气情况，返回温度和天气状况")
    public String getCurrentWeather(String city) {
        int temp = ThreadLocalRandom.current().nextInt(-5, 35);
        String condition = CONDITIONS.get(ThreadLocalRandom.current().nextInt(CONDITIONS.size()));
        String result = String.format("%s 当前天气：%s，气温 %d°C", city, condition, temp);
        recorder.record("getCurrentWeather", city, result);
        return result;
    }
}
