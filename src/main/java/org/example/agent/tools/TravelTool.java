package org.example.agent.tools;

/**
 * @author <a href="mailto:me@flycat.tech">Bryon Zen</a>
 * @since 2026/02/27
 */
public class TravelTool {
    public static String getAttraction(String city, String weather) {
        if ("广州".equals(city)) {
            return """
                    广州塔 : 广州地标，可俯瞰全城景观及欣赏珠江夜景 -
                    白云山 : 广州市肺，适合登高远眺和呼吸清新空气 -
                    陈家祠 : 岭南建筑艺术明珠，展示精美的木雕与石雕工艺 -
                    越秀公园 : 历史悠久，内有广州城标五羊雕像及古城墙 -
                    沙面岛 : 充满异国情调的建筑群，适合漫步摄影感受悠闲氛围 -
                    永庆坊 : 广州旧城微改造的范本。这里有李小龙祖居，既有传统的西关大屋，也有现代的咖啡馆和文创店，古今交融感极强。
                    """.replace("\n", " ");
        } else if ("深圳".equals(city)) {
            return """
                    深圳湾公园 : 拥有超长滨海步道，是徒步、骑行及观赏红树林白鹭的绝佳去处 -
                    世界之窗 : 汇集世界各地名胜古迹的缩影，夜晚的烟花表演和灯光秀非常震撼 -
                    仙湖植物园 : 绿植茂密的天然氧吧，内部的弘法寺香火鼎盛，适合登高祈福 -
                    大梅沙 : 拥有广阔的沙滩和清澈的海水，是感受亚热带海滨风情的热门选择 -
                    莲花山公园 : 位于城市中轴线，山顶广场可以近距离俯瞰福田中心的繁华全景 -
                    """.replace("\n", " ");
        }

        return "暂无结果";
    }
}
