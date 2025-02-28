package de.linzn.openai;

import com.azure.ai.openai.models.FunctionDefinition;
import org.json.JSONObject;

public interface IFunctionCall {

    JSONObject completeRequest(JSONObject input);

    FunctionDefinition getFunctionString();

    String functionName();
}
