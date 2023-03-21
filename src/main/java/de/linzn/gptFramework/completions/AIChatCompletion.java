package de.linzn.gptFramework.completions;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.linzn.gptFramework.GPTManager;
import de.linzn.gptFramework.GPTPersonality;
import de.stem.stemSystem.STEMSystemApp;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

public class AIChatCompletion {
    private final GPTManager gptManager;
    private final GPTPersonality gptPersonality;
    private final OpenAiService openAiService;
    private final LinkedList<ChatMessage> dataMemory;

    public AIChatCompletion(GPTManager gptManager) {
        this.gptManager = gptManager;
        this.gptPersonality = new GPTPersonality();
        this.dataMemory = new LinkedList<>();
        this.openAiService = new OpenAiService(this.gptManager.getOpenAIToken());
    }


    public ChatMessage requestCompletion(List<String> inputData) {

        for (String input : inputData) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRole("user");
            chatMessage.setContent(input);
            this.dataMemory.addLast(chatMessage);
        }

        LinkedList<ChatMessage> dataToSend = new LinkedList<>(this.dataMemory);
        dataToSend.addFirst(this.gptPersonality.getPersonalityDescription());

        ChatCompletionRequest completionRequest = this.buildRequest(dataToSend);

        ChatMessage result;
        try {
            List<ChatCompletionChoice> results = this.openAiService.createChatCompletion(completionRequest).getChoices();
            result =  results.get(0).getMessage();
            this.dataMemory.addLast(result);
        } catch(Exception e){
            STEMSystemApp.LOGGER.ERROR(e);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent("An error was catch in kernel stacktrace! Please check STEM logs for more informations!");
            result = chatMessage;
        }

        return result;
    }

    private ChatCompletionRequest buildRequest(List<ChatMessage> dataList) {
        return ChatCompletionRequest.builder()
                .messages(dataList)
                .model("gpt-3.5-turbo")
                .n(1)
                .user("STEM-SYSTEM")
                .build();
    }
}
