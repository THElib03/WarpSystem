package eu.buildsouthwest.warp.core.storage;

import eu.buildsouthwest.warp.core.model.Warp;

import java.util.Optional;
import java.util.UUID;

public class CaffeineWarpCache implements WarpCache{
    @Override
    public Optional<Warp> getWarp(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public void put(UUID uuid, Warp warp) {

    }

    @Override
    public void invalidateAll() {

    }

    @Override
    public long size() {
        return 0;
    }
}