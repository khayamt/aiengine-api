package za.co.kpolit.aiengine.controller;

import za.co.kpolit.aiengine.service.ChatGPTService;
import za.co.kpolit.aiengine.model.QuizQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
public class ChatGPTController {
    private static final Logger logger = LoggerFactory.getLogger(ChatGPTController.class);

    @Autowired
    private ChatGPTService chatGPTService;

   // @RequestMapping("/api/codegen")
    @PostMapping("/api/codegen")
    public String generateCode(@RequestBody String prompt) {
        logger.info("Calling generateCode with " + prompt);
        return chatGPTService.generateCode(prompt);
    }

    @GetMapping("/api/quiz")
    public ResponseEntity<String> getQuiz(@RequestParam(defaultValue = "math") String topic) {
        logger.info("Calling getQuiz with " + topic);
        String quizJson = chatGPTService.generateQuiz(topic);
        return ResponseEntity.ok(quizJson);
    }
    @GetMapping("/api/get-quiz-questions")
    public ResponseEntity<List<QuizQuestion>> getQuizQuestions(@RequestParam(defaultValue = "general knowledge") String topic) {
        logger.info("Calling getQuizQuestions with " + topic);
        try {
            List<QuizQuestion> questions = chatGPTService.getQuizQuestions(topic);
            logger.info("Returning quiz " + questions);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/chat")
    public String chatWithAI(@RequestBody String prompt)
    {
        logger.info("Calling chatWithAI with " + prompt);
        return chatGPTService.chatWithAI(prompt);
    }
    /*public ResponseEntity<String> chatWithAI(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openAiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
    {
      "model": "gpt-4o-mini",
      "messages": [
        {"role": "system", "content": "You are an AI tutor helping students learn."},
        {"role": "user", "content": "%s"}
      ]
    }
    """.formatted(message);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", entity, String.class
        );

        return ResponseEntity.ok(response.getBody());
    }*/

}
