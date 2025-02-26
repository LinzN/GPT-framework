package de.linzn.gptFramework.completions;


import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.KeyCredential;
import de.linzn.gptFramework.GPTManager;
import de.linzn.gptFramework.GPTPersonality;
import de.linzn.gptFramework.memory.DatabaseMemory;
import de.linzn.openai.ChatMessage;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;

import java.util.LinkedList;
import java.util.List;

public class AIChatCompletion {

    private final STEMPlugin identityPlugin;
    private final String identity;
    private GPTManager gptManager;
    private GPTPersonality gptPersonality;
    private DatabaseMemory databaseMemory;
    private OpenAIClient openAiService;

    public AIChatCompletion(GPTManager gptManager, STEMPlugin identityPlugin, String identity) {
        this.identityPlugin = identityPlugin;
        this.identity = identity;
        this.gptManager = gptManager;
        this.gptPersonality = new GPTPersonality();
        this.openAiService = new OpenAIClientBuilder()
                .credential(new KeyCredential(this.gptManager.getOpenAIToken()))
                .buildClient();
        this.databaseMemory = new DatabaseMemory(this);
    }

    public String requestCompletion(List<String> inputData) {
        STEMSystemApp.LOGGER.CONFIG("Using new chat completion");
        for (String input : inputData) {
            ChatMessage chatMessage = new ChatMessage(input, ChatRole.USER);
            this.databaseMemory.memorize(chatMessage);
        }

        LinkedList<ChatMessage> dataToSend = this.databaseMemory.getMemory();
        dataToSend.addFirst(this.gptPersonality.getPersonalityDescription());
        ChatCompletionsOptions completionRequest = this.buildRequest(dataToSend);

        ChatMessage message;
        try {
            ChatCompletions chatCompletions = this.openAiService.getChatCompletions(this.gptPersonality.getModel(), completionRequest);
            ChatChoice choice = chatCompletions.getChoices().get(0);
            message = new ChatMessage(choice.getMessage());
            this.databaseMemory.memorize(message);
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
            message = new ChatMessage("An error was catch in kernel stacktrace! Please check STEM logs for more information!", ChatRole.ASSISTANT);
        }

        return message.getContent();
    }

    private ChatCompletionsOptions buildRequest(List<ChatMessage> dataList) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(ChatMessage.convertToRequestMessage(dataList));
        options.setN(1);
        options.setUser("STEM-SYSTEM");
        options.setModel(this.gptPersonality.getModel());
        return options;
    }

    public void destroy() {
        this.gptPersonality = null;
        this.openAiService = null;
        this.gptManager = null;
        this.databaseMemory = null;
    }

    public STEMPlugin getIdentityPlugin() {
        return identityPlugin;
    }

    public String getIdentity() {
        return identity;
    }
}
