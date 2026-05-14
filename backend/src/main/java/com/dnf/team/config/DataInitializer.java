package com.dnf.team.config;

import com.dnf.team.entity.Profession;
import com.dnf.team.repository.ProfessionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProfessionRepository professionRepository;

    public DataInitializer(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    @Override
    public void run(String... args) {
        if (professionRepository.count() > 0) {
            return; // Already initialized
        }

        // 鬼剑士
        createProfession("狂战士", "鬼剑士", "#C41F3B", "⚔️", "燃烧自身血气强化战斗能力的剑士");
        createProfession("鬼泣", "鬼剑士", "#9482C9", "👻", "操纵鬼神之力的阵法师");
        createProfession("剑魂", "鬼剑士", "#69CCF0", "🗡️", "精通所有剑类武器的大师");
        createProfession("阿修罗", "鬼剑士", "#FF7D0A", "👁️", "拥有波动力场的剑士");

        // 格斗家
        createProfession("气功师", "格斗家", "#ABD473", "🌊", "运用念气进行战斗的格斗家");
        createProfession("散打", "格斗家", "#C79C6E", "🥊", "以极致拳脚功夫著称的格斗家");
        createProfession("街霸", "格斗家", "#FFF569", "💀", "擅长使用各种卑鄙手段的格斗家");
        createProfession("柔道家", "格斗家", "#FF8C00", "🤼", "精通擒拿摔投术的格斗家");

        // 魔法师
        createProfession("元素师", "魔法师", "#69CCF0", "🔮", "操纵四大元素的魔法师");
        createProfession("召唤师", "魔法师", "#FFD700", "📿", "召唤各种精灵协助战斗");
        createProfession("战斗法师", "魔法师", "#FF6EB4", "💫", "结合魔法与体术的战斗型法师");
        createProfession("魔道学者", "魔法师", "#8B4513", "🧪", "利用炼金术和魔法道具战斗的学者");

        // 圣职者
        createProfession("圣骑士", "圣职者", "#F58CBA", "🛡️", "以神圣之力守护队友的骑士");
        createProfession("蓝拳圣使", "圣职者", "#0070DE", "👊", "使用圣拳连击的近战圣职者");
        createProfession("驱魔师", "圣职者", "#A9A9A9", "🔨", "驱除邪恶的符咒与式神使者");

        // 枪手
        createProfession("漫游枪手", "枪手", "#FFF569", "🔫", "以华丽射击技巧著称的枪手");
        createProfession("枪炮师", "枪手", "#FF4500", "💣", "使用重火器进行范围打击的枪手");
        createProfession("机械师", "枪手", "#0070DE", "🤖", "操纵各种机械装置的枪手");
        createProfession("弹药专家", "枪手", "#ABD473", "💥", "使用各种特殊弹药的专家");

        System.out.println("=== DNF职业数据初始化完成 ===");
    }

    private void createProfession(String name, String category, String color, String icon, String description) {
        Profession p = new Profession();
        p.setName(name);
        p.setCategory(category);
        p.setColor(color);
        p.setIcon(icon);
        p.setDescription(description);
        professionRepository.save(p);
    }
}
