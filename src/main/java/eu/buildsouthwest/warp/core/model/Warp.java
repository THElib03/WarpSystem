package eu.buildsouthwest.warp.core.model;

import java.util.Optional;
import java.util.UUID;

public record Warp(
        UUID uuid,
        String displayName,
        String world,
        String description,
        String type,
        double x,
        double y,
        double z,
        double pitch,
        double yaw,
        String icon,
        Optional<GeoMetadata> geoMetadata,
        Optional<EducationalData> eduData,
        Optional<CityHubData> cityHubData
) {}