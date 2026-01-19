package com.teg.popcornium_api.intentions.service;

import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.intentions.model.MovieFilterDto;
import com.teg.popcornium_api.intentions.model.MovieFilteringResultDto;
import com.teg.popcornium_api.intentions.utils.MovieFilteringMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieFilteringService {

    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<MovieFilteringResultDto> filter(MovieFilterDto filters) {
        return movieRepository.findAllWithDetails()
                .stream()
                .filter(MovieFilterEngine.build(filters))
                .map(MovieFilteringMapper::map)
                .toList();
    }
}
