package de.jeff_media.demoserver;

import de.jeff_media.jefflib.LocationUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {

    private static final DemoServer main = DemoServer.getInstance();
    private final Location spawnLocation = LocationUtils.getLocationFromSection(main.getConfig().getConfigurationSection("spawn"), null);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§2§lWelcome to the §6§lJEFF Media GbR DEMO Server§2§l!\n" +
                "§2Enter §d§l/demo §2to try out my plugins. §a§l:-)");
        event.getPlayer().teleport(spawnLocation);
        event.getPlayer().getInventory().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(spawnLocation);
    }
}
