-- Main Warp Table

CREATE TABLE IF NOT EXISTS bte_warps (
    uuid CHAR(36) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    world VARCHAR(64) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    pitch FLOAT NOT NULL,
    yaw FLOAT NOT NULL
);

-- GeoLocation Warp Data Table

CREATE TABLE IF NOT EXISTS geo_warp (
    warpId CHAR(36) PRIMARY KEY,
);