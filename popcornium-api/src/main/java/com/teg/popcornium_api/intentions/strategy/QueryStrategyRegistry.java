package com.teg.popcornium_api.intentions.strategy;

import com.teg.popcornium_api.intentions.model.Intention;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QueryStrategyRegistry {

    private final Map<Intention, QueryStrategy> strategies;

    public QueryStrategyRegistry(List<QueryStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(QueryStrategy::getIntention, s -> s));
    }

    public QueryStrategy get(Intention intention) {
        return strategies.getOrDefault(intention, strategies.get(Intention.GENERAL));
    }
}
