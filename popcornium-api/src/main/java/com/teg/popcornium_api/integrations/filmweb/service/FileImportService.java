package com.teg.popcornium_api.integrations.filmweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teg.popcornium_api.common.model.Actor;
import com.teg.popcornium_api.common.model.Description;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.util.DataConversionUtil;
import com.teg.popcornium_api.integrations.filmweb.mapper.MovieImportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileImportService {

    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;
    private final DataConversionUtil conversionUtil;

    @Value("${MOVIE_IMPORT_BASE_PATH:${movie.import.base-path}}")
    private String baseFolderPath;

    @Transactional
    public int importMoviesFromFiles() {
        Path baseDirPath = Paths.get(baseFolderPath);

        if (!Files.isDirectory(baseDirPath)) {
            log.warn("Base directory {} does not exist. Attempting to create...", baseFolderPath);
            try {
                Files.createDirectories(baseDirPath);
                log.info("Successfully created base directory: {}", baseFolderPath);
            } catch (IOException e) {
                log.error("Failed to create base directory: {}", baseFolderPath, e);
                return 0;
            }
        }

        try (Stream<Path> movieDirStream = Files.list(baseDirPath)) {
            return movieDirStream
                    .filter(Files::isDirectory)
                    .mapToInt(this::processMovieDirectory)
                    .sum();

        } catch (IOException e) {
            log.error("Error listing directories in: {}", baseFolderPath, e);
            return 0;
        }
    }

    private int processMovieDirectory(Path movieDirPath) {
        String folderName = movieDirPath.getFileName().toString();

        try (Stream<Path> fileStream = Files.list(movieDirPath)) {
            Optional<Path> jsonFilePath = fileStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".json"))
                    .findFirst();

            if (jsonFilePath.isEmpty()) {
                log.warn("No JSON file found in directory: {}", folderName);
                return 0;
            }

            Path foundJsonPath = jsonFilePath.get();
            MovieImportDto movieDto = objectMapper.readValue(foundJsonPath.toFile(), MovieImportDto.class);

            String englishTitle = movieDto.englishTitle();
            Integer productionYear = conversionUtil.parseProductionYear(movieDto.productionYear()).orElse(null);

            if (englishTitle == null || englishTitle.isBlank() || productionYear == null) {
                log.warn("Skipping directory {}: Missing English title or production year.", folderName);
                return 0;
            }

            Optional<Movie> existingMovie = movieRepository.findByEnglishTitleAndProductionYear(
                    englishTitle,
                    productionYear
            );

            Movie movie = mapToMovieEntity(movieDto);

            if (existingMovie.isPresent()) {
                movie.setId(existingMovie.get().getId());
                movie.setCreated(existingMovie.get().getCreated());
                log.info("Updated existing movie: {} ({})", movie.getPolishTitle(), movie.getEnglishTitle());
            } else {
                log.info("Successfully imported NEW movie: {} ({})", movie.getPolishTitle(), movie.getEnglishTitle());
            }

            movieRepository.save(movie);
            return 1;

        } catch (IOException e) {
            log.error("Error processing directory {}. Details: {}", folderName, e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Error parsing numeric value while processing movie in directory {}. Details: {}", folderName, e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while importing movie from {}. Details: {}", folderName, e.getMessage());
        }

        return 0;
    }

    private Movie mapToMovieEntity(MovieImportDto dto) {
        LocalDateTime now = LocalDateTime.now();

        Double rating = conversionUtil.parseRating(dto.rating()).orElse(null);
        Integer productionYear = conversionUtil.parseProductionYear(dto.productionYear()).orElse(null);

        Movie movie = new Movie();
        movie.setCreated(now);
        movie.setModified(now);
        movie.setPolishTitle(dto.polishTitle());
        movie.setEnglishTitle(dto.englishTitle());
        movie.setProductionYear(productionYear);
        movie.setRating(rating);
        movie.setRatingCount(dto.ratingCount());
        movie.setPosterUrl(dto.posterUrl());

        Set<Description> descriptions = Optional.ofNullable(dto.descriptions())
                .stream()
                .flatMap(java.util.Collection::stream)
                .map(text -> {
                    Description description = new Description();
                    description.setCreated(now);
                    description.setModified(now);
                    description.setText(text);
                    description.setMovie(movie);
                    return description;
                })
                .collect(Collectors.toSet());
        movie.setDescriptions(descriptions);

        Set<Actor> actors = Optional.ofNullable(dto.actors())
                .stream()
                .flatMap(map -> map.values().stream())
                .map(actorDto -> {
                    Actor actor = new Actor();
                    actor.setCreated(now);
                    actor.setModified(now);
                    actor.setFullName(actorDto.fullName());
                    actor.setRole(actorDto.role());
                    actor.setPhotoUrl(actorDto.photoLink());
                    actor.setMovie(movie);
                    return actor;
                })
                .collect(Collectors.toSet());
        movie.setActors(actors);
        movie.setCategories(new HashSet<>());

        return movie;
    }
}