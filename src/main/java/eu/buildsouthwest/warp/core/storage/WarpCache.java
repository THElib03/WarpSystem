package eu.buildsouthwest.warp.core.storage;

import eu.buildsouthwest.warp.core.model.Warp;

import java.util.Optional;
import java.util.UUID;

public interface WarpCache {

    Optional<Warp> getWarp(UUID uuid);
    void put(UUID uuid, Warp warp);
    void invalidateAll();
    long size();
}