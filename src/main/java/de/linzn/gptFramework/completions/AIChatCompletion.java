package de.linzn.gptFramework.completions;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.linzn.gptFramework.GPTManager;
import de.linzn.gptFramework.GPTPersonality;
import de.linzn.gptFramework.memory.DatabaseMemory;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class AIChatCompletion {

    private final STEMPlugin identityPlugin;
    private final String identity;
    private GPTManager gptManager;
    private GPTPersonality gptPersonality;
    private DatabaseMemory databaseMemory;
    private OpenAiService openAiService;

    public AIChatCompletion(GPTManager gptManager, STEMPlugin identityPlugin, String identity) {
        this.identityPlugin = identityPlugin;
        this.identity = identity;
        this.gptManager = gptManager;
        this.gptPersonality = new GPTPersonality();
        this.openAiService = new OpenAiService(this.gptManager.getOpenAIToken(), Duration.ofMinutes(2));
        this.databaseMemory = new DatabaseMemory(this);
    }

    public String requestCompletion(List<String> inputData) {

        for (String input : inputData) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRole("user");
            chatMessage.setContent(input);
            this.databaseMemory.memorize(chatMessage);
        }

        LinkedList<ChatMessage> dataToSend = this.databaseMemory.getMemory();
        dataToSend.addFirst(this.gptPersonality.getPersonalityDescription());
        ChatCompletionRequest completionRequest = this.buildRequest(dataToSend);

        ChatMessage result;
        try {
            List<ChatCompletionChoice> results = this.openAiService.createChatCompletion(completionRequest).getChoices();
            result = results.get(0).getMessage();
            this.databaseMemory.memorize(result);
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent("An error was catch in kernel stacktrace! Please check STEM logs for more information!");
            result = chatMessage;
        }

        return result.getContent();
    }

    private ChatCompletionRequest buildRequest(List<ChatMessage> dataList) {
        return ChatCompletionRequest.builder()
                .messages(dataList)
                .model(this.gptPersonality.getModel())
                .n(1)
                .user("STEM-SYSTEM")
                .build();
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
