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
import de.linzn.openai.ChatMessage;
import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class AIEventCompletion {
    private final GPTPersonality gptPersonality;
    private final OpenAIClient openAiService;

    public AIEventCompletion(GPTManager gptManager) {
        this.gptPersonality = new GPTPersonality("event");
        this.openAiService = new OpenAIClientBuilder()
                .credential(new KeyCredential(gptManager.getOpenAIToken()))
                .buildClient();
    }

    public JSONObject requestEventResponse(String event) {
        STEMSystemApp.LOGGER.CONFIG("Using new chat completion");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", event);

        ChatMessage chatMessage = new ChatMessage(jsonObject.toString(), ChatRole.USER);


        LinkedList<ChatMessage> dataToSend = new LinkedList<>();
        dataToSend.addFirst(this.gptPersonality.getPersonalityDescription());
        dataToSend.add(chatMessage);

        ChatCompletionsOptions completionRequest = this.buildRequest(dataToSend);

        ChatMessage result;

        try {
            ChatCompletions chatCompletions = this.openAiService.getChatCompletions(this.gptPersonality.getModel(), completionRequest);
            ChatChoice choice = chatCompletions.getChoices().get(0);
            result = new ChatMessage(choice.getMessage());
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
            JSONObject errorOutput = new JSONObject();
            errorOutput.put("output", "An error was catch in kernel stacktrace! Event: " + event);
            chatMessage = new ChatMessage(errorOutput.toString(), ChatRole.ASSISTANT);
            result = chatMessage;
        }

        if (result.getContent().startsWith("{") && result.getContent().endsWith("}")) {
            return new JSONObject(result.getContent());
        } else {
            JSONObject invalidOutput = new JSONObject();
            invalidOutput.put("output", result.getContent());
            return invalidOutput;
        }
    }

    private ChatCompletionsOptions buildRequest(List<ChatMessage> dataList) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(ChatMessage.convertToRequestMessage(dataList));
        options.setN(1);
        options.setUser("STEM-SYSTEM");
        options.setModel(this.gptPersonality.getModel());
        return options;
    }


}
