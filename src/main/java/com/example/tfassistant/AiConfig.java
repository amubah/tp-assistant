package com.example.tfassistant;

import org.slf4j.Logger;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AiConfig {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AiConfig.class);

  @Value("${data.url}")
  private Resource dataUrl;

  @Bean
  VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
  }

  @Bean
  ApplicationRunner go(VectorStore vectorStore) {
    return args -> {
      var reader = new TikaDocumentReader(dataUrl);
      var splitter = new TokenTextSplitter();
      LOGGER.info("Loading document into vector store...");
      vectorStore.accept(splitter.split(reader.read()));
      LOGGER.info("Document loaded into vector store.");
    };
  }

}
