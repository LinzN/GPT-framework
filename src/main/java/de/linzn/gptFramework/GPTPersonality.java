package de.linzn.gptFramework;

import com.theokanning.openai.completion.chat.ChatMessage;

public class GPTPersonality {

    private final String model;
    private final String user;
    private final String personalityDescription;

    public GPTPersonality() {
        this.model = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString("personality.model");
        this.user = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString("personality.user");
        this.personalityDescription = GPTFrameworkPlugin.gptFrameworkPlugin.getDefaultConfig().getString("personality.description");
    }

    public ChatMessage getPersonalityDescription() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");
        chatMessage.setContent(this.personalityDescription);
        return chatMessage;
    }

    public String getModel() {
        return model;
    }

    public String getUser() {
        return user;
    }
}
