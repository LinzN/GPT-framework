package de.linzn.gptFramework.completions;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.linzn.gptFramework.GPTManager;
import de.linzn.gptFramework.GPTPersonality;
import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class AIEventCompletion {
    private final GPTPersonality gptPersonality;
    private final OpenAiService openAiService;

    public AIEventCompletion(GPTManager gptManager) {
        this.gptPersonality = new GPTPersonality("event");
        this.openAiService = new OpenAiService(gptManager.getOpenAIToken(), Duration.ofMinutes(2));
    }

    public JSONObject requestEventResponse(String event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", event);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent(jsonObject.toString());


        LinkedList<ChatMessage> dataToSend = new LinkedList<>();
        dataToSend.addFirst(this.gptPersonality.getPersonalityDescription());
        dataToSend.add(chatMessage);

        ChatCompletionRequest completionRequest = this.buildRequest(dataToSend);

        ChatMessage result;
        try {
            List<ChatCompletionChoice> results = this.openAiService.createChatCompletion(completionRequest).getChoices();
            result = results.get(0).getMessage();
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
            chatMessage = new ChatMessage();
            JSONObject errorOutput = new JSONObject();
            errorOutput.put("output", "An error was catch in kernel stacktrace! Event: " + event);
            chatMessage.setContent(errorOutput.toString());
            result = chatMessage;
        }

        if(result.getContent().startsWith("{") && result.getContent().endsWith("}")){
            return new JSONObject(result.getContent());
        } else {
            JSONObject invalidOutput = new JSONObject();
            invalidOutput.put("output", result.getContent());
            return invalidOutput;
        }
    }

    private ChatCompletionRequest buildRequest(List<ChatMessage> dataList) {
        return ChatCompletionRequest.builder()
                .messages(dataList)
                .model("gpt-3.5-turbo-16k")
                .n(1)
                .user("STEM-SYSTEM")
                .build();
    }
}
