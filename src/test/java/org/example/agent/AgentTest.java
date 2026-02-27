package org.example.agent;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AgentTest {
    MyAgent agent = new MyAgent();

    @Test
    public void case1() {
        agent.call("你好，请帮我查询一下今天广州的天气，然后根据天气推荐一个合适的旅游景点。");
    }

    @Test
    public void case2() {
        agent.call("深圳有哪些地方好玩的，请用表格列出来");
    }
}
