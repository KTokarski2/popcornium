package com.teg.popcornium_api.integrations.filmweb.controller;

import com.teg.popcornium_api.integrations.filmweb.service.FileImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final FileImportService fileImportService;

    @PostMapping("/movies")
    public ResponseEntity<String> importMovieData() {
        int count = fileImportService.importMoviesFromFiles();

        if (count > 0) {
            return ResponseEntity.ok(String.format("Pomyślnie zaimportowano %d filmów z katalogu.", count));
        } else {
            return ResponseEntity.badRequest().body("Nie znaleziono żadnych plików do zaimportowania lub wystąpiły błędy. Sprawdź logi aplikacji.");
        }
    }
}
