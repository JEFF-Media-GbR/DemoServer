package de.jeff_media.demoserver.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.jeff_media.demoserver.DemoServer;
import de.jeff_media.demoserver.GUI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@CommandAlias("demo")
public class MainCommand extends BaseCommand {

    private static final DemoServer main = DemoServer.getInstance();

    @Default
    public static void onCommand(Player player, String[] args) {
        GUI.showMainMenu(player);
    }

    @CommandAlias("suicide")
    public static void onSuicide(Player player, String[] args) {
        player.setHealth(0);
    }

    @Subcommand("reload")
    @CommandPermission("reload")
    public static void onReload(Player player, String[] args) {
        main.reloadConfig();
        player.sendMessage("§aDemoServer reloaded.");
    }

    @Subcommand("items")
    public static void onItem(Player player, String[] args) {
        YamlConfiguration yaml = new YamlConfiguration();
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) list.add(item);
        }
        yaml.set("items", list);
        try {
            yaml.save(new File(main.getDataFolder(), "items.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
