package com.teg.popcornium_api.common.bootstrap;

import com.teg.popcornium_api.common.service.GraphResetService;
import com.teg.popcornium_api.common.service.GraphSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraphBootstrap {

    private final GraphResetService graphResetService;
    private final GraphSyncService graphSyncService;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        graphResetService.resetGraph();
        graphSyncService.syncAllMovies();
    }
}