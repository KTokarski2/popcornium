package com.teg.popcornium_api.common.controller;

import com.teg.popcornium_api.common.service.GraphQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphQueryController {

    private final GraphQueryService graphQueryService;

    @GetMapping("/related/{movieId}")
    public ResponseEntity<?> related(@PathVariable String movieId) {
        var list = graphQueryService.getRelatedMovies(movieId);
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Movie not found or no related movies"));
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/by-actor")
        public List<?> byActor(@RequestParam String name,
                           @RequestParam(defaultValue = "20") int limit) {
        return graphQueryService.findMoviesByActor(name, limit);
    }

    @GetMapping("/by-category")
    public List<?> byCategory(@RequestParam String name,
                              @RequestParam(defaultValue = "20") int limit) {
        return graphQueryService.findMoviesByCategory(name, limit);
    }

    @GetMapping("/by-director")
    public List<?> byDirector(@RequestParam String name,
                              @RequestParam(defaultValue = "20") int limit) {
        return graphQueryService.findMoviesByDirector(name, limit);
    }

    @GetMapping("/trending")
    public List<?> trending(@RequestParam(defaultValue = "20") int limit) {
        return graphQueryService.trendingMovies(limit);
    }

    @PostMapping("/vector-search")
    public List<?> vectorSearch(@RequestBody Map<String, Object> body) {
        // Expect { "embedding": [0.1, 0.2, ...], "k": 10 }
        @SuppressWarnings("unchecked")
        List<Double> embedding = (List<Double>) body.get("embedding");
        int k = (int) body.getOrDefault("k", 10);
        return graphQueryService.vectorSearch(embedding, k);
    }

    @PostMapping("/hybrid-search")
    public List<?> hybridSearch(@RequestBody Map<String, Object> body) {
        // Expect: embedding:[], movieId:"", k:10, actorWeight:1.0, directorWeight:2.0, categoryWeight:0.5
        @SuppressWarnings("unchecked")
        List<Double> embedding = (List<Double>) body.get("embedding");
        String movieId = (String) body.get("movieId");
        int k = (int) body.getOrDefault("k", 10);
        double actorWeight = body.getOrDefault("actorWeight", 1.0) instanceof Number ?
                ((Number) body.get("actorWeight")).doubleValue() : Double.parseDouble(body.get("actorWeight").toString());
        double directorWeight = body.getOrDefault("directorWeight", 2.0) instanceof Number ?
                ((Number) body.get("directorWeight")).doubleValue() : Double.parseDouble(body.get("directorWeight").toString());
        double categoryWeight = body.getOrDefault("categoryWeight", 0.5) instanceof Number ?
                ((Number) body.get("categoryWeight")).doubleValue() : Double.parseDouble(body.get("categoryWeight").toString());

        return graphQueryService.hybridSearch(embedding, movieId, k, actorWeight, directorWeight, categoryWeight);
    }
}
