package de.linzn.openai;

public class ChatMessageNoContentException extends IllegalArgumentException {
    public ChatMessageNoContentException() {
        super("ChatMessage has no content!");
    }
}
