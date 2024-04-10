package com.privacypolicies.PrivacyPoliciesNotification.Service;

import jakarta.annotation.PostConstruct;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class OpenNlpService {

    private POSTaggerME posTagger;
    private TokenizerME tokenizer;

    @PostConstruct
    public void init() throws Exception {
        // Tokenization model
        try (InputStream modelIn = new ClassPathResource("models/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin").getInputStream()) {
            TokenizerModel tokenizerModel = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(tokenizerModel);
        }

        // POS tagging model
        try (InputStream modelIn = new ClassPathResource("models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin").getInputStream()) {
            POSModel posModel = new POSModel(modelIn);
            posTagger = new POSTaggerME(posModel);
        }
    }

    public String[] tokenize(String text) {
        return tokenizer.tokenize(text);
    }

    public String[] tagPos(String[] tokens) {
        return posTagger.tag(tokens);
    }
}