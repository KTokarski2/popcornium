package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.neo4jmodel.MovieNode;
import com.teg.popcornium_api.common.neo4jrepository.MovieNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphSyncService {

    private final GraphDataLoader graphDataLoader;
    private final MovieNodeRepository movieNodeRepository;

    @Transactional(transactionManager = "neo4jTransactionManager")
    public void syncAllMovies() {
        List<MovieNode> nodes = graphDataLoader.loadAllMovieNodes();
        movieNodeRepository.saveAll(nodes);
    }
}
