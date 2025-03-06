package com.example.tfassistant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.FactCheckingEvaluator;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@SpringBootTest
public class AiServiceImplTest {

  @Autowired
  AiServiceImpl aiService;

  @Autowired
  ChatClient.Builder chatClientBuilder;

  @Value("classpath:/data.json")
  Resource dataResource;

  @ParameterizedTest
  @ValueSource(
      strings = {
          "What does the eligibility verification agent (EVA) do?",
          "What does the claims processing agent (CAM) do?",
          "How does PHIL work?",
          "Tell me about Thoughtful AI's Agents."
      }
  )
  public void testComplete(String question) throws IOException {
    String dataJson = dataResource.getContentAsString(Charset.defaultCharset());

    RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    FactCheckingEvaluator factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);

    String answer = aiService.complete(question);
    EvaluationResponse relevancyResponse = relevancyEvaluator.evaluate(new EvaluationRequest(question, answer));
    Assertions.assertThat(relevancyResponse.isPass()).isTrue();

    EvaluationResponse factCheckingResponse = factCheckingEvaluator.evaluate(
        new EvaluationRequest(question, List.of(new Document(dataJson)), answer));
    Assertions.assertThat(factCheckingResponse.isPass()).isTrue();
  }

}
