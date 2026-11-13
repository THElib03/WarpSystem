package eu.buildsouthwest.warp.core.command;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;
import eu.buildsouthwest.warp.WarpSystem;
import eu.buildsouthwest.warp.core.database.WarpRepository;
import eu.buildsouthwest.warp.util.NameSanitizer;
import eu.buildsouthwest.warp.util.NameValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateWarpCommand implements CommandExecutor {
    private final Component NONPLAYER = Component.text("This command can only be run by a player.", NamedTextColor.RED);
    private final Component INVALID = Component.text("Invalid warp name! Please use standard characters from the Latin alphabet.", NamedTextColor.RED);
    private WarpSystem plugin;
    private WarpRepository repo;

    public CreateWarpCommand(WarpSystem plugin) {
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

        UUID uuid = UUID.randomUUID();
        Optional<String> possibleName = parseName(args);
        if(possibleName.isEmpty()){
            pl.sendMessage(INVALID);
            return false;
        }
        String name = possibleName.get();

        String world = pl.getWorld().getName();

        double x = Math.round(pl.getLocation().getX() * 100.0) / 100.0;
        double y = Math.round(pl.getLocation().getY() * 100.0) / 100.0;
        double z = Math.round(pl.getLocation().getZ() * 100.0) / 100.0;
        double pitch = Math.round(pl.getLocation().getPitch() * 100.0) / 100.0;
        double yaw = Math.round(pl.getLocation().getYaw() * 100.0) / 100.0;

        UUID creatorId = pl.getUniqueId();
        String creatorName = pl.getName();


        
        return true;
    }

    private Optional<String> parseName(String[] args) {
        if(args.length < 1) return Optional.empty();

        String joined = Arrays.stream(args)
                .map(NameSanitizer::sanitize)
                .filter(w -> w.chars().anyMatch(Character::isLetterOrDigit))
                .map(w -> capitalize(w.toLowerCase(Locale.ENGLISH)))
                .collect(Collectors.joining());

        return NameValidator.validate(joined);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
    }
}
