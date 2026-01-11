package com.teg.popcornium_api.seeder.filmweb.mapper;

import com.teg.popcornium_api.common.model.*;
import com.teg.popcornium_api.common.model.types.Language;
import com.teg.popcornium_api.common.repository.ActorRepository;
import com.teg.popcornium_api.common.repository.CategoryRepository;
import com.teg.popcornium_api.common.repository.DirectorRepository;
import com.teg.popcornium_api.common.util.DataConversionUtil;
import com.teg.popcornium_api.seeder.filmweb.dto.MovieImportDto;
import com.teg.popcornium_api.seeder.filmweb.dto.ActorImportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;
    private final CategoryRepository categoryRepository;
    private final DataConversionUtil conversionUtil;

    public Movie mapToMovieEntity(MovieImportDto dto) {
        LocalDateTime now = LocalDateTime.now();

        Double rating = conversionUtil.parseRating(dto.rating()).orElse(null);
        Integer productionYear = conversionUtil.parseProductionYear(dto.productionYear()).orElse(null);

        Movie movie = new Movie();
        movie.setPolishTitle(dto.polishTitle());
        movie.setOriginalTitle(dto.originalTitle());
        movie.setProductionYear(productionYear);
        movie.setRating(rating);
        movie.setRatingCount(dto.ratingCount());
        movie.setPosterUrl(dto.posterUrl());

        findOrCreateAndSetDirector(movie, dto.directorName());

        movie.setDescriptions(mapDescriptions(movie, dto.descriptions(), now));
        movie.setMovieActors(mapMovieActors(movie, dto.actors()));
        movie.setMovieCategories(mapMovieCategories(movie, dto.categories()));

        return movie;
    }

    private void findOrCreateAndSetDirector(Movie movie, String directorName) {
        if (directorName != null && !directorName.isBlank()) {
            Director director = directorRepository.findByName(directorName)
                    .orElseGet(() -> {
                        Director newDirector = new Director();
                        newDirector.setName(directorName);
                        return directorRepository.save(newDirector);
                    });
            movie.setDirector(director);
        }
    }

    private Set<Description> mapDescriptions(Movie movie, List<String> descriptionTexts, LocalDateTime now) {
        return Optional.ofNullable(descriptionTexts)
                .stream()
                .flatMap(java.util.Collection::stream)
                .map(text -> {
                    Description description = new Description();
                    description.setText(text);
                    description.setLanguage(Language.PL);
                    description.setMovie(movie);
                    return description;
                })
                .collect(Collectors.toSet());
    }

    private Set<MovieActor> mapMovieActors(Movie movie, Map<String, ActorImportDto> actors) {
        return Optional.ofNullable(actors)
                .stream()
                .flatMap(map -> map.values().stream())
                .map(actorDto -> {
                    Actor actor = actorRepository.findByName(actorDto.fullName())
                            .orElseGet(() -> {
                                Actor newActor = new Actor();
                                newActor.setName(actorDto.fullName());
                                newActor.setPhotoUrl(actorDto.photoLink());;
                                return actorRepository.save(newActor);
                            });

                    MovieActor movieActor = new MovieActor();
                    movieActor.setRoleName(actorDto.role());

                    movieActor.setMovie(movie);
                    movieActor.setActor(actor);

                    return movieActor;
                })
                .collect(Collectors.toSet());
    }

    private Set<MovieCategory> mapMovieCategories(Movie movie, List<String> categories) {
        return Optional.ofNullable(categories)
                .stream()
                .flatMap(java.util.Collection::stream)
                .map(categoryName -> {
                    Category category = categoryRepository.findByName(categoryName)
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(categoryName);
                                return categoryRepository.save(newCategory);
                            });

                    MovieCategory movieCategory = new MovieCategory();
                    movieCategory.setMovie(movie);
                    movieCategory.setCategory(category);
                    return movieCategory;
                })
                .collect(Collectors.toSet());
    }
}