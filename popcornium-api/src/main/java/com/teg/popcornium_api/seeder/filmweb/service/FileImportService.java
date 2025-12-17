package com.teg.popcornium_api.seeder.filmweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.util.DataConversionUtil;
import com.teg.popcornium_api.seeder.filmweb.dto.MovieImportDto;
import com.teg.popcornium_api.seeder.filmweb.mapper.MovieMapper;
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
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileImportService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
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

            String originalTitle = movieDto.originalTitle();
            Integer productionYear = conversionUtil.parseProductionYear(movieDto.productionYear()).orElse(null);

            if (originalTitle == null || originalTitle.isBlank() || productionYear == null) {
                log.warn("Skipping directory {}: Missing English title or production year.", folderName);
                return 0;
            }

            Optional<Movie> existingMovie = movieRepository.findByOriginalTitleAndProductionYear(
                    originalTitle,
                    productionYear
            );

            Movie newMovieData = movieMapper.mapToMovieEntity(movieDto);

            if (existingMovie.isPresent()) {
                Movie existing = existingMovie.get();

                newMovieData.setId(existing.getId());

                existing.getMovieActors().clear();
                existing.getMovieCategories().clear();
                existing.getDescriptions().clear();

                newMovieData.getMovieActors().forEach(ma -> ma.setMovie(existing));
                existing.getMovieActors().addAll(newMovieData.getMovieActors());

                newMovieData.getMovieCategories().forEach(mc -> mc.setMovie(existing));
                existing.getMovieCategories().addAll(newMovieData.getMovieCategories());

                newMovieData.getDescriptions().forEach(d -> d.setMovie(existing));
                existing.getDescriptions().addAll(newMovieData.getDescriptions());

                existing.setPolishTitle(newMovieData.getPolishTitle());
                existing.setOriginalTitle(newMovieData.getOriginalTitle());
                existing.setProductionYear(newMovieData.getProductionYear());
                existing.setRating(newMovieData.getRating());
                existing.setRatingCount(newMovieData.getRatingCount());
                existing.setPosterUrl(newMovieData.getPosterUrl());
                existing.setDirector(newMovieData.getDirector());

                movieRepository.save(existing);
                log.info("Updated existing movie: {} ({})", existing.getPolishTitle(), existing.getOriginalTitle());
            } else {
                movieRepository.save(newMovieData);
                log.info("Successfully imported NEW movie: {} ({})", newMovieData.getPolishTitle(), newMovieData.getOriginalTitle());
            }

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
}