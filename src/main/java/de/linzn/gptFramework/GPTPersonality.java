package de.linzn.gptFramework;

import com.azure.ai.openai.models.ChatRole;
import de.linzn.openai.ChatMessage;

public class GPTPersonality {

    private final String model;
    private final String user;
    private final String personalityDescription;

    public GPTPersonality() {
        this.model = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString("personality.model");
        this.user = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString("personality.user");
        this.personalityDescription = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString("personality.description");
    }

    public GPTPersonality(String name) {
        this.model = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString(name + ".personality.model");
        this.user = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString(name + ".personality.user");
        this.personalityDescription = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString(name + ".personality.description");
    }

    public ChatMessage getPersonalityDescription() {
        return new ChatMessage(this.personalityDescription, ChatRole.DEVELOPER);
    }

    public String getModel() {
        return model;
    }

    public String getUser() {
        return user;
    }
}
