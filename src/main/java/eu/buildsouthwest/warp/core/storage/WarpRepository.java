package eu.buildsouthwest.warp.core.storage;

import eu.buildsouthwest.warp.core.model.Warp;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WarpRepository {

    CompletableFuture<Optional<Warp>> findByID(UUID id);

    CompletableFuture<Void> save(Warp warp);

    CompletableFuture<Void> delete(Warp warp);

    CompletableFuture<Map<UUID, Warp>> loadAllAsync();
}