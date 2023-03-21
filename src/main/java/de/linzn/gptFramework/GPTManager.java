package de.linzn.gptFramework;

import de.linzn.gptFramework.completions.AIChatCompletion;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;

import java.util.HashMap;
import java.util.Map;

public class GPTManager {

    private final String openAIToken;

    private final Map<String, AIChatCompletion> aiChatCompletionMap;

    public GPTManager(STEMPlugin stemPlugin) {
        this.openAIToken = stemPlugin.getDefaultConfig().getString("openAI.token");
        this.aiChatCompletionMap = new HashMap<>();
    }

    public synchronized AIChatCompletion getAIChatCompletion(String identity) {
        if (!this.aiChatCompletionMap.containsKey(identity)) {
            AIChatCompletion aiChatCompletion = new AIChatCompletion(this);
            this.aiChatCompletionMap.put(identity, aiChatCompletion);
        }
        return this.aiChatCompletionMap.get(identity);
    }

    public synchronized boolean hasAIChatCompletion(String identity){
        return this.aiChatCompletionMap.containsKey(identity);
    }

    public synchronized void destroyAIChatCompletion(String identity){
        if(this.aiChatCompletionMap.containsKey(identity)){
            AIChatCompletion aiChatCompletion = this.aiChatCompletionMap.get(identity);
            aiChatCompletion.destroy();
            this.aiChatCompletionMap.remove(identity);
        }
    }

    public String getOpenAIToken() {
        return openAIToken;
    }
}

