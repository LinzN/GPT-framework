package de.linzn.gptFramework.completions;


import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ImageGenerationData;
import com.azure.ai.openai.models.ImageGenerationOptions;
import com.azure.ai.openai.models.ImageGenerations;
import com.azure.core.credential.KeyCredential;
import de.linzn.gptFramework.GPTManager;
import de.stem.stemSystem.STEMSystemApp;

import java.util.List;

public class AIImageCompletion {
    private GPTManager gptManager;

    private OpenAIClient openAiService;

    public AIImageCompletion(GPTManager gptManager) {
        this.gptManager = gptManager;
        this.openAiService = new OpenAIClientBuilder()
                .credential(new KeyCredential(this.gptManager.getOpenAIToken()))
                .buildClient();
    }

    public String requestCompletion(String prompt) {
        STEMSystemApp.LOGGER.CONFIG("Using new image completion");
        ImageGenerationOptions options = this.buildRequest(prompt);
        StringBuilder url = new StringBuilder();
        try {
            ImageGenerations imageGenerations = this.openAiService.getImageGenerations("dall-e-3", options);
            List<ImageGenerationData> results = imageGenerations.getData();
            for (ImageGenerationData result : results) {
                url.append(" ").append(result.getUrl());
            }
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
            url = new StringBuilder("An error was catch in kernel stacktrace! Please check STEM logs for more informations!");
        }

        return url.toString();
    }

    private ImageGenerationOptions buildRequest(String prompt) {
        ImageGenerationOptions option = new ImageGenerationOptions(prompt);
        option.setN(1);
        option.setModel("dall-e-3");
        option.setUser("STEM-SYSTEM");
        return option;
    }

    public void destroy() {
        this.openAiService = null;
        this.gptManager = null;
    }
}
