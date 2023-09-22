package de.linzn.gptFramework;

import de.linzn.gptFramework.completions.AIChatCompletion;
import de.linzn.gptFramework.completions.AIEventCompletion;
import de.linzn.gptFramework.completions.AIImageCompletion;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GPTManager {

    private final String openAIToken;
    private final Map<STEMPlugin, Map<String, AIChatCompletion>> aiChatMap;

    public GPTManager(STEMPlugin stemPlugin) {
        this.openAIToken = stemPlugin.getDefaultConfig().getString("openAI.token");
        this.aiChatMap = new ConcurrentHashMap<>();
    }

    public synchronized boolean hasAIChatCompletion(STEMPlugin stemPlugin, String identity) {
        if (this.aiChatMap.containsKey(stemPlugin)) {
            return this.aiChatMap.get(stemPlugin).containsKey(identity);
        }
        return false;
    }

    public synchronized Map<String, AIChatCompletion> getAllAIChatCompletionByPlugin(STEMPlugin stemPlugin) {
        if (this.aiChatMap.containsKey(stemPlugin)) {
            return this.aiChatMap.get(stemPlugin);
        } else {
            return new HashMap<>();
        }
    }

    public synchronized void destroyAIChatCompletion(STEMPlugin stemPlugin, String identity) {
        if (!this.aiChatMap.containsKey(stemPlugin)) {
            return;
        }
        if (!this.aiChatMap.get(stemPlugin).containsKey(identity)) {
            return;
        }
        this.aiChatMap.get(stemPlugin).get(identity).destroy();
        this.aiChatMap.get(stemPlugin).remove(identity);
        if (this.aiChatMap.get(stemPlugin).size() == 0) {
            this.aiChatMap.remove(stemPlugin);
        }
    }

    public synchronized AIChatCompletion createAIChatCompletion(STEMPlugin stemPlugin, String identity) {
        if (!this.aiChatMap.containsKey(stemPlugin)) {
            this.aiChatMap.put(stemPlugin, new ConcurrentHashMap<>());
        }
        Map<String, AIChatCompletion> pluginMap = this.aiChatMap.get(stemPlugin);
        if (!pluginMap.containsKey(identity)) {
            pluginMap.put(identity, new AIChatCompletion(this));
        }
        return pluginMap.get(identity);
    }

    public synchronized AIImageCompletion createAIImageCompletion() {
        return new AIImageCompletion(this);
    }

    public synchronized AIEventCompletion createAIEventCompletion(){
        return new AIEventCompletion(this);
    }

    public String getOpenAIToken() {
        return openAIToken;
    }
}

