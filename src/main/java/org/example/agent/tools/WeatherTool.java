package org.example.agent.tools;


/**
 * @author <a href="mailto:me@flycat.tech">Bryon Zen</a>
 * @since 2026/02/27
 */
public class WeatherTool {
    public static String getWeather(String city) {
        // 仅作演示所以直接返回固定信息，也可以接入真实API
        return "%s当前天气:晴朗, 气温:28摄氏度".formatted(city);
    }
}
