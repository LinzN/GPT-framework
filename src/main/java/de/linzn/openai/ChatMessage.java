package de.linzn.openai;

import com.azure.ai.openai.models.*;
import com.azure.core.util.BinaryData;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private ChatRequestMessage chatRequestMessage = null;
    private FunctionCall functionCall;

    public ChatMessage(ChatResponseMessage chatResponseMessage) {
        this(chatResponseMessage.getContent(), chatResponseMessage.getRole());

        if(chatResponseMessage.getFunctionCall() != null){
            functionCall = chatResponseMessage.getFunctionCall();
        }
    }

    public ChatMessage(ChatRequestMessage chatRequestMessage) {
        this.chatRequestMessage = chatRequestMessage;
    }

    public ChatMessage(String content, ChatRole role) {
        if (role == ChatRole.ASSISTANT) {
            chatRequestMessage = new ChatRequestAssistantMessage(content);
        } else if (role == ChatRole.USER) {
            chatRequestMessage = new ChatRequestUserMessage(content);
        } else if (role == ChatRole.SYSTEM) {
            chatRequestMessage = new ChatRequestSystemMessage(content);
        } else if (role == ChatRole.DEVELOPER) {
            chatRequestMessage = new ChatRequestDeveloperMessage(BinaryData.fromString(content));
        } else {
            chatRequestMessage = new ChatRequestSystemMessage(content);
        }
    }

    public static List<ChatRequestMessage> convertToRequestMessage(List<ChatMessage> messages) {
        List<ChatRequestMessage> converted = new ArrayList<>();
        for (ChatMessage chatMessage : messages) {
            converted.add(chatMessage.convertToRequestMessage());
        }
        return converted;
    }

    public ChatRole getRole() {
        return chatRequestMessage.getRole();
    }

    public String getContent() {
        String content;
        if(this.isFunctionCall()){
            content = this.functionCall.getArguments();
        } else if (chatRequestMessage.getRole() == ChatRole.ASSISTANT) {
            content = ((ChatRequestAssistantMessage) chatRequestMessage).getContent().toString();
        } else if (chatRequestMessage.getRole() == ChatRole.USER) {
            content = ((ChatRequestUserMessage) chatRequestMessage).getContent().toString();
        } else if (chatRequestMessage.getRole() == ChatRole.SYSTEM) {
            content = ((ChatRequestSystemMessage) chatRequestMessage).getContent().toString();
        } else if (chatRequestMessage.getRole() == ChatRole.DEVELOPER) {
            content = ((ChatRequestDeveloperMessage) chatRequestMessage).getContent().toString();
        } else if (chatRequestMessage.getRole() == ChatRole.TOOL) {
            content = ((ChatRequestToolMessage) chatRequestMessage).getContent().toString();
        }else if (chatRequestMessage.getRole() == ChatRole.FUNCTION) {
                content = ((ChatRequestFunctionMessage) chatRequestMessage).getContent();
        } else {
            throw new ChatMessageNoContentException();
        }
        return content;
    }

    public ChatRequestMessage convertToRequestMessage() {
        return chatRequestMessage;
    }

    public boolean isFunctionCall(){
        return this.functionCall != null;
    }

    public FunctionCall getFunctionCall(){
        return this.functionCall;
    }
}
