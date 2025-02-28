package de.linzn.gptFramework.functions;


import com.azure.ai.openai.models.FunctionDefinition;
import de.linzn.gptFramework.GPTFrameworkPlugin;
import de.linzn.gptFramework.completions.AIImageCompletion;
import de.linzn.openai.IFunctionCall;
import de.linzn.openai.models.FunctionParameters;
import de.linzn.openai.models.FunctionProperties;
import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;

public class CreateImage implements IFunctionCall {
    @Override
    public JSONObject completeRequest(JSONObject input) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imageDescription", input.getString("imageDescription"));

        STEMSystemApp.LOGGER.CORE(input);
        String url = new AIImageCompletion(GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager()).requestCompletion(input.getString("imageDescription"));
        jsonObject.put("success", true);
        jsonObject.put("imageURL", url);

        return jsonObject;
    }

    @Override
    public FunctionDefinition getFunctionString() {
        return new FunctionDefinition(this.functionName())
                .setDescription("Create or draw an image based of a given description and returns as a web url")
                .setParameters(new FunctionParameters()
                        .setType("object")
                        .addProperty(new FunctionProperties()
                                .setName("imageDescription")
                                .setType("string")
                                .setDescription("The description to create the image like 'A black cat sitting in front of a house while sunrises'")
                                .setRequired(true))
                        .build());
    }

    @Override
    public String functionName() {
        return "create_image";
    }

}
