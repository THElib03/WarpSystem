package eu.buildsouthwest.warp;

import com.alpsbte.alpslib.io.database.DatabaseConfigPaths;
import com.alpsbte.alpslib.io.database.DatabaseConnection;
import com.alpsbte.alpslib.io.database.DatabaseSection;
import com.google.common.io.CharStreams;
import eu.buildsouthwest.warp.util.WarpConfig;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.Configuration;
import space.arim.dazzleconf.StandardErrorPrint;
import space.arim.dazzleconf.backend.Backend;
import space.arim.dazzleconf.backend.PathRoot;
import space.arim.dazzleconf.backend.yaml.YamlBackend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

public final class WarpSystem extends JavaPlugin {

    private static WarpSystem plugin;
    private static WarpConfig warpConfig;
    @Override
    public void onEnable() {
        plugin = this;

        // Splash Screen
        getComponentLogger().info("Enabling Build SouthWest Europe - Warp System");
        getComponentLogger().info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        getComponentLogger().info("");
        getComponentLogger().info(" __    __                   __           _                 ");
        getComponentLogger().info("/ / /\\ \\ \\__ _ _ __ _ __   / _\\_   _ ___| |_ ___ _ __ ___  ");
        getComponentLogger().info("\\ \\/  \\/ / _` | '__| '_ \\  \\ \\| | | / __| __/ _ \\ '_ ` _ \\ ");
        getComponentLogger().info(" \\  /\\  / (_| | |  | |_) | _\\ \\ |_| \\__ \\ ||  __/ | | | | |");
        getComponentLogger().info("  \\/  \\/ \\__,_|_|  | .__/  \\__/\\__, |___/\\__\\___|_| |_| |_|");
        getComponentLogger().info("");
        getComponentLogger().info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        // Initialize Config
        getComponentLogger().info("Inizializing Config");
        Configuration<WarpConfig> configuration = Configuration.defaultBuilder(WarpConfig.class).build();
        Path configPath = getDataPath().resolve("config.yml");
        Backend backend = new YamlBackend(new PathRoot(configPath));
        warpConfig = configuration.configureOrFallback(backend, new StandardErrorPrint(output -> {
            try {
                output.printTo(System.out);
            } catch (IOException e) {
                getComponentLogger().warn("Could not print the error",e);
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }));





        // Initialize Database Connection
        getComponentLogger().info("Initializing the Database Connection...");
        try {
            initDatabase();
            getComponentLogger().info("SQL database connection Started!");
        } catch (Exception e) {
            getComponentLogger().error("Could not initialize database connection.", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DatabaseConnection.shutdown();
    }

    public static WarpConfig getWarpConfig() {
        return warpConfig;
    }

    private void initDatabase() throws SQLException, IOException {
        WarpConfig config = getWarpConfig();
        DatabaseSection databaseSection = new DatabaseSection(config.database().url(),
                config.database().dbname(),
                config.database().username(),
                config.database().password(),
                config.database().maxLifetime(),
                config.database().connectionTimeout(),
                config.database().keepaliveTime(),
                config.database().maximumPoolSize(),
                0,
                "plotsystem-hikari");
        DatabaseConnection.initializeDatabase(databaseSection, true);
        var initScript = CharStreams.toString(Objects.requireNonNull(getTextResource("DATABASE.sql")));
        try (var con = DatabaseConnection.getConnection(); var s = con.createStatement()) {
            s.execute(initScript);
        }
    }
}
