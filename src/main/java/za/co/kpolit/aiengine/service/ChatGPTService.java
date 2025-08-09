package za.co.kpolit.aiengine.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionChoice;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import za.co.kpolit.aiengine.model.QuizQuestion;

import java.util.*;

@Service
public class ChatGPTService {
    private OpenAiService openAiService;
    @Value("${openai.api.key}")
    private String openaiApiKey;
    @Value("${openai.api.model}")
    private String model;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init(){
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            openAiService = new OpenAiService(openaiApiKey);
        } else {
            System.out.println("WARNING: OPENAI_API_KEY not set");
        }
    }
    public String generateQuiz(String topic) {
        if (openAiService == null) {
            return "// ERROR: OpenAI API key not configured.";
        }

        CompletionRequest request = CompletionRequest.builder()
                .prompt("Generate 2 multiple-choice questions on '" + topic + "' with 4 options and correct answers in JSON format.")
                .model("text-davinci-003")
                .maxTokens(500)
                .build();

        List<CompletionChoice> choices = openAiService.createCompletion(request).getChoices();
        return choices.get(0).getText();
    }
    public List<QuizQuestion> getQuizQuestions(String topic) throws Exception {
        OpenAiService service = new OpenAiService(openaiApiKey);

        List<ChatMessage> messages = List.of(
                new ChatMessage("system", "You are a quiz generator AI."),
                //new ChatMessage("user", "Generate 2 multiple-choice questions on '" + topic + "' with 4 options each. Format as JSON: [{question, options, answer}]")
                new ChatMessage("user", "Generate 2 multiple-choice questions on '" + topic + "' with 4 options each. Return only raw JSON. Do not include markdown formatting or explanations. Format: [{question, options, answer}]")

        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(500)
                .temperature(0.7)
                .build();

        String responseText = service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();

        // Strip Markdown code block if present
        responseText = responseText.trim();
        if (responseText.startsWith("```") && responseText.endsWith("```")) {
                responseText = responseText.substring(3, responseText.length() - 3).trim();
        }

        return mapper.readValue(responseText, new TypeReference<List<QuizQuestion>>(){});
    }

    public String generateCode(String prompt) {
        if (openAiService == null) {
            return "// ERROR: OpenAI API key not configured.";
        }

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(
                        new ChatMessage("system", "You are a helpful assistant that writes Java code."),
                        new ChatMessage("user", prompt)
                ))
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }

    public String chatWithAI( String prompt)
    {
        if (openAiService == null) {
            return "// ERROR: OpenAI API key not configured.";
        }

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(
                        new ChatMessage("system", "You are an AI tutor helping students learn."),
                        new ChatMessage("user", prompt)
                ))
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }

}
