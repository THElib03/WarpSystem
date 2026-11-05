CREATE USER IF NOT EXISTS 'db_user'@'localhost' IDENTIFIED BY 'change-me';
CREATE DATABASE IF NOT EXISTS warp;
USE warp;

CREATE TABLE IF NOT EXISTS bte_warps (
    id UUID PRIMARY KEY,
    `name` VARCHAR(64) NOT NULL,
    world VARCHAR(64) NOT NULL,
    `description` VARCHAR(128),
    `type` VARCHAR(32) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    pitch FLOAT NOT NULL,
    yaw FLOAT NOT NULL,
    icon VARCHAR(64) DEFAULT 'minecraft:stone',
    visits INT DEFAULT 0,
    creationDate DATETIME NOT NULL DEFAULT now()
    creatorId UUID NOT NULL,
    CONSTRAINT `author_FK` FOREIGN KEY (`creatorId`) REFERENCES `author` (`creatorId`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS geo_meta (
    warpId UUID PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    state VARCHAR(16) NOT NULL,
    CONSTRAINT `geo_FK` FOREIGN KEY (`warpId`) REFERENCES `bte_warps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS educational (
    warpId UUID PRIMARY KEY, 
    CONSTRAINT `edu_FK` FOREIGN KEY (`warpId`) REFERENCES `bte_warps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS city_hub (
    warpId UUID PRIMARY KEY,
    CONSTRAINT `city_FK` FOREIGN KEY (`warpId`) REFERENCES `bte_warps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS author (
	creatorId UUID PRIMARY KEY,
	creatorName VARCHAR(16) NOT NULL,
);

GRANT SELECT, INSERT, UPDATE ON warp.* TO 'db_user'@'localhost';
