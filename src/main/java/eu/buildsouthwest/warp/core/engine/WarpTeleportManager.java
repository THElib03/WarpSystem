package eu.buildsouthwest.warp.core.engine;

import eu.buildsouthwest.warp.core.storage.WarpRepository;
import org.bukkit.plugin.Plugin;

public class WarpTeleportManager {

    private final Plugin plugin;
    private final WarpRepository repo;

    public WarpTeleportManager(Plugin plugin, WarpRepository repo) {
        this.plugin = plugin;
        this.repo = repo;
    }
    
}