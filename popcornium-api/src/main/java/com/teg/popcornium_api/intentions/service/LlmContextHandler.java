package com.teg.popcornium_api.intentions.service;

import com.teg.popcornium_api.intentions.IntentionDetector;
import com.teg.popcornium_api.intentions.model.ExecutionStep;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.LlmContext;
import com.teg.popcornium_api.rag.types.RagType;
import com.teg.popcornium_api.rag.api.GraphRagService;
import com.teg.popcornium_api.rag.api.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LlmContextHandler {

    private final IntentionDetector intentionDetector;
    private final RagService ragService;
    private LlmContext llmContext;
    private RagType ragType;
    private Optional<ExecutionStep> step;
    private final GraphRagService graphRagService;

    private static final String ERROR_EXECUTION_STEP_MISSING = "Missing current execution step";

    public void createFreshContext(String userQuery, RagType ragType) {
        this.llmContext = LlmContext.empty();
        this.step = Optional.empty();
        this.ragType = ragType;
        llmContext.setDetectedBaseIntention(intentionDetector.detect(userQuery));
    }

    public Intention getBaseIntention() {
        return llmContext.getDetectedBaseIntention();
    }

    public Optional<String> handleBaseIntentionContext(String userQuery, Intention intention, String history) {
        if (baseShouldRag()) {
            switch(this.ragType) {
                case GRAPH -> llmContext.setFinalContext(doGraphRag(userQuery));
                case NORMAL -> llmContext.setFinalContext(doNormalRag(userQuery, intention));
            }
            return Optional.of(appendHistoryToContext(llmContext.getFinalContext(), history));
        }
        return Optional.of(history);
    }

    public Optional<String> handleComplexIntentionContext(String userQuery) {
        ExecutionStep currentStep = getStepIfPresent();
        Optional<String> retrievedRagContext = retrieveRagContextIfAllowed(currentStep, userQuery);
        List<String> previousResults = collectDependencyResults(currentStep);
        return preparePartialResponse(retrievedRagContext, previousResults);
    }
    
    public Optional<String> buildFinalComplexContext(String history) {
        if (!llmContext.hasAny()) {
            return Optional.empty();
        }
        llmContext.buildFinalContext();
        return Optional.of(appendHistoryToContext(
                this.llmContext.getFinalContext(),
                history
        ));
    }

    public void setCurrentStep(ExecutionStep currentStep) {
        this.step = Optional.of(currentStep);
    }

    public void addPartialToContext(String key, String value) {
        this.llmContext.putAttribute(key, value);
    }

    private boolean baseShouldRag() {
        return llmContext.getDetectedBaseIntention() != Intention.FILTERING;
    }

    private String doNormalRag(String userQuery, Intention intention) {
        return ragService.retrieveContext(userQuery, intention);
    }

    private String doGraphRag(String userQuery) {
        return graphRagService.retrieveContext(userQuery);
    }

    private ExecutionStep getStepIfPresent() {
        return this.step.orElseThrow(() ->
                new RuntimeException(ERROR_EXECUTION_STEP_MISSING));
    }

    private Optional<String> retrieveRagContextIfAllowed(ExecutionStep step, String userQuery) {
        if (!step.allowRag()) {
            return Optional.empty();
        }
        String ragContext = switch (this.ragType) {
            case NORMAL -> doNormalRag(userQuery, step.intention());
            case GRAPH -> doGraphRag(userQuery);
        };
        return ragContext.isBlank() ? Optional.empty() : Optional.of(ragContext);
    }

    private List<String> collectDependencyResults(ExecutionStep step) {
        if (step.dependsOn().isEmpty() || !llmContext.hasAny()) {
            return Collections.emptyList();
        }
        return step.dependsOn().stream()
                .map(dependency -> llmContext.getAttribute(dependency))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Optional<String> preparePartialResponse(Optional<String> ragContent, List<String> previousResults) {
        List<String> nonEmptyResults = previousResults.stream()
                .filter(s -> !s.isBlank())
                .toList();
        if (ragContent.isEmpty() && nonEmptyResults.isEmpty()) {
            return Optional.empty();
        }
        StringBuilder sb = new StringBuilder();
        ragContent.ifPresent(s -> sb.append("\n\nReceived RAG context:\n\n")
                .append(s)
                .append("\n\n"));
        if (!nonEmptyResults.isEmpty()) {
            sb.append("Context from other sources:\n\n");
            nonEmptyResults.forEach(result -> sb.append(result).append("\n"));
            sb.append("\n");
        }

        sb.append("\nUse this information to answer the question\n\n");

        return Optional.of(sb.toString());
    }

    private String appendHistoryToContext(String context, String history) {
        StringBuilder sb = new StringBuilder(history).append("\n");
        sb.append(context);
        return sb.toString();
    }
}
