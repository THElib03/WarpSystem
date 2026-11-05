package eu.buildsouthwest.warp.core.database;

import eu.buildsouthwest.warp.WarpSystem;
import eu.buildsouthwest.warp.core.model.Warp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand implements CommandExecutor {
    private final Component NONPLAYER = Component.text("This command can only be run by a player.", NamedTextColor.RED);
    private WarpSystem plugin;
    private WarpRepository repo;

    public TestCommand(WarpSystem plugin) {
        this.plugin = plugin;
        this.repo = new WarpRepository(plugin.getDatabaseService());
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cm, String label, String[] args) {
        plugin.getLogger().info("Getting all warp info, 2");
        if(!(cs instanceof Player pl)){
            cs.sendMessage(NONPLAYER);
            return true;
        }

//        if(!pl.hasPermission("warp.test")) return true;
        repo.getAllWarps()
                .thenAccept( warps -> Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getLogger().info("warps size: " + warps.size());
                    for (Warp warp : warps){
                        plugin.getLogger().info("Obtained warp named: " + warp.displayName() + " and " + warp.x() + " position.");
                    }
                }))
                .exceptionally( ex -> {
                    plugin.getLogger().severe("Failed to load warps: " + ex.getMessage());
                    return null;
                });

        return true;
    }
}
