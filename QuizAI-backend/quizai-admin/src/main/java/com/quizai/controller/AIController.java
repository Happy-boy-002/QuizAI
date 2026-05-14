package com.quizai.controller;

import com.quizai.domain.R;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/ai")
public class AIController {

    @Autowired
    private OpenAiChatModel chatModel;

    @GetMapping("explain")
    public R<String> explain(
            @RequestParam String questionContent,
            @RequestParam String userAnswer,
            @RequestParam String correctAnswer,
            @RequestParam String[] options) {

        String prompt = buildPrompt(questionContent, userAnswer, correctAnswer, options);
        String result = chatModel.call(prompt);
        return R.success(result);
    }

    @GetMapping(value = "explainStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> explainStream(
            @RequestParam String questionContent,
            @RequestParam String userAnswer,
            @RequestParam String correctAnswer,
            @RequestParam String[] options) {

        String prompt = buildPrompt(questionContent, userAnswer, correctAnswer, options);
        return chatModel.stream(new Prompt(new UserMessage(prompt)))
                .map(this::toSseEvent)
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .data("[DONE]")
                        .build()));
    }

    private ServerSentEvent<String> toSseEvent(ChatResponse response) {
        String content = response.getResults().stream()
                .findFirst()
                .map(g -> g.getOutput().getText())
                .orElse("");
        return ServerSentEvent.<String>builder()
                .data(content)
                .build();
    }

    private String buildPrompt(String questionContent, String userAnswer, String correctAnswer, String[] options) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位专业的编程技术面试辅导老师。请对下面这道题目进行详细解析。\n\n");
        sb.append("【题目】\n").append(questionContent).append("\n\n");
        sb.append("【选项】\n");
        char label = 'A';
        for (String option : options) {
            sb.append(label).append(". ").append(option).append("\n");
            label++;
        }
        sb.append("\n");
        sb.append("【用户的答案】").append(userAnswer).append("\n");
        sb.append("【正确答案】").append(correctAnswer).append("\n\n");
        sb.append("请分析：\n");
        sb.append("1. 这道题考察的知识点是什么\n");
        sb.append("2. 每个选项的分析（为什么对/错）\n");
        sb.append("3. 解题思路总结\n");
        sb.append("4. 如果用户答错了，指出错误原因并给出学习建议\n");
        return sb.toString();
    }
}
