package eu.buildsouthwest.warp.util;

import space.arim.dazzleconf.engine.liaison.SubSection;

public interface WarpConfig {

    @SubSection Database database();

    interface Database {
        default String url() { return "jdbc:mariadb://localhost:3306/"; }
        default String dbname() { return "plotsystem"; }
        default String username() { return "plotsystem"; }
        default String password() { return "minecraft"; }
        default int maxLifetime() { return 1800000; }
        default int connectionTimeout() { return 30000; }
        default int keepaliveTime() { return 120000; }
        default int maximumPoolSize() { return 10; }
    }

}