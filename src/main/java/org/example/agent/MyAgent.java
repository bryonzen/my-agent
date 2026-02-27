package org.example.agent;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.example.agent.tools.TravelTool;
import org.example.agent.tools.WeatherTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:me@flycat.tech">Bryon Zen</a>
 * @since 2026/02/27
 */
public class MyAgent {
    //region 根据您使用的服务，将这里替换成对应的凭证和地址
    private static final String BASE_URL = "https://api.deepseek.com";
    private static final String API_KEY = "";
    private static final String MODEL_ID = "deepseek-chat";
    //endregion

    // 构造大模型客户端
    private final OpenAIClient client = OpenAIOkHttpClient.builder().apiKey(API_KEY).baseUrl(BASE_URL).build();

    // 系统提示词
    private static final String SYS_PROMPT = """
            你是一个智能旅行助手。你的任务是分析用户的请求，并使用可用工具一步步地解决问题。
            
            # 可用工具:
            - `get_weather(city: str)`: 查询指定城市的实时天气。
            - `get_attraction(city: str, weather: str)`: 根据城市和天气搜索推荐的旅游景点。
            
            # 输出格式要求:
            你的每次回复必须严格遵循以下格式，包含一对Thought和Action：
            
            Thought: [你的思考过程和下一步计划]
            Action: [你要执行的具体行动]
            
            Action的格式必须是以下之一：
            1. 调用工具：function_name(arg_name="arg_value")
            2. 结束任务：Finish[最终答案]
            
            # 重要提示:
            - 每次只输出一对Thought-Action
            - Action必须在同一行，不要换行
            - Thought-Action之间不要多个换行
            - 不要包含Observation
            - 当收集到足够信息可以回答用户问题时，必须使用 Action: Finish[最终答案] 格式结束
            
            请开始吧！
            """;

    /**
     * 调用智能体
     * @param userMessage 用户输入
     * @return 返回执行后的结果
     */
    public String call(String userMessage) {
        System.out.println("[用户输入]: " + userMessage);

        List<String> promptHistory = new ArrayList<>();

        promptHistory.add("用户请求: " + userMessage);

        // 设置最大循环次数
        for (int i = 0; i < 10; i++) {
            System.out.printf("\n --- 循环%s --- \n", i + 1);

            // 构建完整 prompt
            String fullPrompt = String.join("\n", promptHistory);
            // 本轮循环观测结果
            String observation;

            // 调用大模型返回结果
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addSystemMessage(SYS_PROMPT)
                    .addUserMessage(fullPrompt)
                    .model(MODEL_ID)
                    .build();

            ChatCompletion chatCompletion = client.chat().completions().create(params);

            String output = chatCompletion.choices().get(0).message().content().orElse("");

            System.out.println("\n[模型输出]: \n" + output);

            // 将模型输出加入prompt 历史
            promptHistory.add(output);

            // 解析执行动作
            Matcher matcher = Pattern.compile("Action: (.*)", Pattern.DOTALL).matcher(output);
            if (!matcher.find()) {
                observation = "错误: 未能解析到 Action 字段。请确保你的回复严格遵循 'Thought: ... Action: ...' 的格式。";
                String observationStr = "Observation: " + observation;
                System.out.println(observationStr);
                promptHistory.add(observationStr);
                continue;
            }

            // 判断是否最终答案
            String actionStr = matcher.group(1).trim();
            if (actionStr.startsWith("Finish")) {
                Matcher finalAnswerMatcher = Pattern.compile("Finish\\[(.*)]", Pattern.DOTALL).matcher(actionStr);
                if (!finalAnswerMatcher.find()) {
                    observation = "错误: 未能解析到 最终答案 字段。请确保你的回复严格遵循 'Finish[最终答案]' 的格式。";
                    String observationStr = "Observation: " + observation;
                    System.out.println(observationStr);
                    promptHistory.add(observationStr);
                    continue;
                }
                String finalAnswer = finalAnswerMatcher.group(1);
                System.out.println("\n[最终答案]: \n" + finalAnswer);
                return finalAnswer;
            }

            // 解析工具名称
            Matcher toolNameMatcher = Pattern.compile("(\\w+)\\(", Pattern.DOTALL).matcher(actionStr);
            if (!toolNameMatcher.find()) {
                observation = "错误: 未能解析到 function_name 字段。请确保你的回复严格遵循 'function_name(arg_name=\"arg_value\")' 的格式。";
                String observationStr = "Observation: " + observation;
                System.out.println(observationStr);
                promptHistory.add(observationStr);
                continue;
            }
            String toolName = toolNameMatcher.group(1);

            // 解析参数列表
            Matcher argsMatcher = Pattern.compile("\\((.*)\\)", Pattern.DOTALL).matcher(actionStr);
            if (!argsMatcher.find()) {
                observation = "错误: 未能解析到 参数列表。请确保你的回复严格遵循 'function_name(arg_name=\"arg_value\")' 的格式。";
                String observationStr = "Observation: " + observation;
                System.out.println(observationStr);
                promptHistory.add(observationStr);
                continue;
            }
            String argsStr = argsMatcher.group(1);
            List<String> argList = Arrays.stream(argsStr.split(",")).map(arg -> arg.split("=")[1].replace("\"", "")).toList();

            // 执行工具
            if ("get_weather".equals(toolName)) {
                observation = WeatherTool.getWeather(argList.get(0));
            } else if ("get_attraction".equals(toolName)) {
                observation = TravelTool.getAttraction(argList.get(0), argList.get(1));
            } else {
                observation = "错误:未定义的工具 '%s'".formatted(toolName);
            }

            // 记录观测结果
            String observationStr = "Observation: " + observation;
            System.out.println(observationStr);
            promptHistory.add(observationStr);
        }

        return "超出最大重复次数";
    }
}
