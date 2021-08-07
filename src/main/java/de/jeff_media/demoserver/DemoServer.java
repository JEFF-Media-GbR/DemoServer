package de.jeff_media.demoserver;

import co.aikar.commands.PaperCommandManager;
import de.jeff_media.demoserver.commands.MainCommand;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DemoServer extends JavaPlugin {

    public static World DEMOWORLD_FLAT, TEMPWORLD, DEMOWORLD;
    public static List<DemoPlugin> plugins;
    @Getter
    private static DemoServer instance;

    public DemoPlugin getPlugin(ItemStack item) {
        if (item == null) return null;
        if (!item.hasItemMeta()) return null;
        if (!item.getItemMeta().hasCustomModelData()) return null;
        for (DemoPlugin plugin : plugins) {
            if (plugin.modeldata() == item.getItemMeta().getCustomModelData()) {
                return plugin;
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void onLoad() {
        instance = this;
        FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), "tempworld"));
        new File(getDataFolder(), "config.yml").delete();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new MainCommand());
        DEMOWORLD_FLAT = createWorld("demoworld", WorldType.FLAT);
        DEMOWORLD = createWorld("demoworld2", WorldType.LARGE_BIOMES);
        TEMPWORLD = createWorld("tempworld", WorldType.NORMAL);

        plugins = new ArrayList<>();
        for (String key : getConfig().getConfigurationSection("plugins").getKeys(false)) {
            ConfigurationSection section = getConfig().getConfigurationSection("plugins").getConfigurationSection(key);
            DemoPlugin plugin = DemoPlugin.getFromConfig(section);
            plugins.add(plugin);
        }
        Bukkit.getPluginManager().registerEvents(new SpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUI(), this);

    }

    private World createWorld(String name, WorldType type) {
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.type(type);
        worldCreator.generateStructures(false);
        World world = Bukkit.createWorld(worldCreator);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setAutoSave(false);
        world.setFullTime(6000);
        return world;
    }
}
