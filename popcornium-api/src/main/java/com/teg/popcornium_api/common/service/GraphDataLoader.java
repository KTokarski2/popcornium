    package com.teg.popcornium_api.common.service;

    import com.teg.popcornium_api.common.model.*;
    import com.teg.popcornium_api.common.neo4jmodel.*;
    import com.teg.popcornium_api.common.repository.DescriptionRepository;
    import com.teg.popcornium_api.common.repository.MovieRepository;
    import com.teg.popcornium_api.common.repository.WikipediaArticleRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.*;

    /**
     * Loads JPA entities inside a JPA transaction and maps them to Neo4j node POJOs.
     * This class must be a separate Spring bean so that @Transactional is applied.
     */
    @Component
    @RequiredArgsConstructor
    public class GraphDataLoader {

        private final MovieRepository movieRepository;
        private final DescriptionRepository descriptionRepository;
        private final WikipediaArticleRepository wikipediaArticleRepository;

        /**
         * Runs inside a JPA transaction. It:
         *  - fetches movies (without LOB joins),
         *  - for each movie explicitly fetches descriptions and wiki articles (LOBs) using repositories,
         *  - maps to Neo4j node objects and returns the list.
         */
        @Transactional(transactionManager = "transactionManager", readOnly = true)
        public List<MovieNode> loadAllMovieNodes() {
            List<Movie> movies = movieRepository.findAllWithGraphData();

            Map<String, ActorNode> actorCache = new HashMap<>();
            Map<String, DirectorNode> directorCache = new HashMap<>();
            Map<String, CategoryNode> categoryCache = new HashMap<>();
            Map<String, LanguageNode> languageCache = new HashMap<>();

            List<com.teg.popcornium_api.common.neo4jmodel.MovieNode> result = new ArrayList<>(movies.size());

            for (Movie movie : movies) {
                com.teg.popcornium_api.common.neo4jmodel.MovieNode mn = new com.teg.popcornium_api.common.neo4jmodel.MovieNode();
                mn.setId(movie.getId());
                mn.setOriginalTitle(movie.getOriginalTitle());
                mn.setPolishTitle(movie.getPolishTitle());
                mn.setProductionYear(movie.getProductionYear());
                mn.setRating(movie.getRating());
                mn.setRatingCount(movie.getRatingCount());
                mn.setPosterUrl(movie.getPosterUrl());

                // Actors (MovieActor -> Actor)
                for (MovieActor ma : movie.getMovieActors()) {
                    Actor actor = ma.getActor();
                    if (actor == null) continue;
                    ActorNode an = actorCache.computeIfAbsent(actor.getId(), id -> {
                        ActorNode a = new ActorNode();
                        a.setName(actor.getName());
                        return a;
                    });
                    mn.getActors().add(an);
                }

                // Director
                Director d = movie.getDirector();
                if (d != null) {
                    DirectorNode dn = directorCache.computeIfAbsent(d.getId(), id -> {
                        DirectorNode dd = new DirectorNode();
                        dd.setName(d.getName());
                        return dd;
                    });
                    mn.setDirector(dn);
                }

                // Categories
                for (MovieCategory mc : movie.getMovieCategories()) {
                    Category cat = mc.getCategory();
                    if (cat == null) continue;
                    CategoryNode cn = categoryCache.computeIfAbsent(cat.getId(), id -> {
                        CategoryNode cc = new CategoryNode();
                        cc.setName(cat.getName());
                        return cc;
                    });
                    mn.getCategories().add(cn);
                }

                // Descriptions -> fetch explicitly (LOBs read inside this JPA tx)
                List<Description> descriptions = descriptionRepository.findByMovieId(movie.getId());
                for (Description desc : descriptions) {
                    DescriptionNode dn = new DescriptionNode();
                    dn.setText(desc.getText());

                    LanguageNode ln = languageCache.computeIfAbsent(desc.getLanguage().name(), code -> {
                        LanguageNode l = new LanguageNode();
                        l.setCode(code);
                        l.setFullName(desc.getLanguage().getFullName());
                        return l;
                    });
                    dn.setLanguage(ln);
                    mn.getDescriptions().add(dn);
                }

                // Wikipedia articles -> fetch per language using existing repo method
                for (Language lang : Language.values()) {
                    Optional<WikipediaArticle> maybe = wikipediaArticleRepository.findByMovieIdAndLanguage(movie.getId(), lang);
                    maybe.ifPresent(wa -> {
                        WikipediaArticleNode wan = new WikipediaArticleNode();
                        wan.setText(wa.getText());

                        LanguageNode ln = languageCache.computeIfAbsent(wa.getLanguage().name(), code -> {
                            LanguageNode l = new LanguageNode();
                            l.setCode(code);
                            l.setFullName(wa.getLanguage().getFullName());
                            return l;
                        });
                        wan.setLanguage(ln);
                        mn.getWikipediaArticles().add(wan);
                    });
                }

                result.add(mn);
            }

            return result;
        }
    }
