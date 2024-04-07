package de.linzn.gptFramework.completions;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import de.linzn.gptFramework.GPTManager;
import de.stem.stemSystem.STEMSystemApp;

import java.time.Duration;
import java.util.List;

public class AIImageCompletion {
    private GPTManager gptManager;
    private OpenAiService openAiService;

    public AIImageCompletion(GPTManager gptManager) {
        this.gptManager = gptManager;
        this.openAiService = new OpenAiService(this.gptManager.getOpenAIToken(), Duration.ofMinutes(2));
    }

    public String requestCompletion(String prompt) {
        CreateImageRequest imageRequest = this.buildRequest(prompt);
        StringBuilder url = new StringBuilder();
        try {
            List<Image> results = this.openAiService.createImage(imageRequest).getData();
            for(Image result : results){
                url.append(" ").append(result.getUrl());
            }
            //url = results.get(0).getUrl();
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
            url = new StringBuilder("An error was catch in kernel stacktrace! Please check STEM logs for more informations!");
        }

        return url.toString();
    }

    private CreateImageRequest buildRequest(String prompt) {
        return CreateImageRequest.builder()
                .prompt(prompt)
                .model("dall-e-3")
                .n(1)
                .user("STEM-SYSTEM")
                .build();
    }

    public void destroy() {
        this.openAiService = null;
        this.gptManager = null;
    }
}
