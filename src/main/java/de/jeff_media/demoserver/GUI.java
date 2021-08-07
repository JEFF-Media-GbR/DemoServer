package de.jeff_media.demoserver;

import de.jeff_media.jefflib.ItemBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GUI implements Listener {

    private static final DemoServer main = DemoServer.getInstance();

    public static void tryPlugin(Player player, MyPlugin plugin) {
        if (plugin.getLocation() != null) {
            player.teleport(plugin.getLocation());
        }
        player.getInventory().clear();
        List<ItemStack> items = plugin.getItems();
        player.getInventory().addItem(items.toArray(new ItemStack[0]));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 2, 1);
        player.closeInventory();
        if (plugin.getGameMode() != null) {
            player.setGameMode(plugin.getGameMode());
        }
        List<String> message = plugin.getMessage();
        player.sendMessage(new String[]{
                " ", " ", " ", " ", " "
        });
        player.sendMessage("§7=== You are now trying out §6§l" + plugin.getName() + "§r§7 ===");
        if (message != null && message.size() > 0) {
            player.sendMessage(message.toArray(new String[0]));
        }
    }

    public static ItemStack getPlaceholder() {
        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§a ");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static int getSlot(int row, int column) {
        if (row < 1 || row > 6 || column < 1 || column > 9) throw new IllegalArgumentException();
        return column - 1 + (row - 1) * 9;
    }

    public static void showMainMenu(Player player) {

        int requiredSize = 54;
        //while(main.plugins.size()>requiredSize) requiredSize+=9;
        Inventory inv = Bukkit.createInventory(new GUIHolder("pluginlist", null), requiredSize, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Choose a plugin:");
        Set<Integer> placeholders = new HashSet<>();
        for (int slot = getSlot(1, 1); slot <= getSlot(1, 9); slot++) {
            placeholders.add(slot);
        }
        for (int slot = getSlot(4, 1); slot <= getSlot(4, 9); slot++) {
            placeholders.add(slot);
        }
        placeholders.add(getSlot(2, 1));
        placeholders.add(getSlot(2, 9));
        placeholders.add(getSlot(3, 1));
        placeholders.add(getSlot(3, 9));
        placeholders.add(getSlot(3, 2));
        placeholders.add(getSlot(3, 8));
        for (int slot : placeholders) {
            inv.setItem(slot, getPlaceholder());
        }
        for (MyPlugin plugin : DemoServer.plugins) {
            inv.addItem(getPluginItem(plugin));
        }
        inv.setItem(getSlot(5, 2), new ItemBuilder(Material.IRON_PICKAXE).setCustomModelData(1002).setName("§6Gamemode: §aSurvival").build());
        inv.setItem(getSlot(5, 3), new ItemBuilder(Material.DIAMOND_PICKAXE).setCustomModelData(1003).setName("§6Gamemode: §eCreative").build());
        inv.setItem(getSlot(5, 8), new ItemBuilder(Material.NAUTILUS_SHELL).setCustomModelData(1001).setName("§bJoin my Discord").build());
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                inv.setItem(i, getPlaceholder());
            }
        }
        player.openInventory(inv);
    }

    public static void showPluginMenu(Player player, MyPlugin plugin) {
        Inventory inv = Bukkit.createInventory(new GUIHolder("plugin", plugin), 9, "§6§l" + plugin.getName());
        ItemStack spigotLink = new ItemBuilder(Material.NAUTILUS_SHELL).setCustomModelData(101).setName("§rView §6" + plugin.getName() + "§r on SpigotMC").build();
        inv.setItem(getSlot(1, 3), spigotLink);
        ItemStack tryNow = new ItemBuilder(Material.NAUTILUS_SHELL).setCustomModelData(102).setName("§a§lTry §6§l" + plugin.getName() + " §a§lnow!").build();
        inv.setItem(getSlot(1, 7), tryNow);
        player.openInventory(inv);
    }

    public static ItemStack getPluginItem(MyPlugin plugin) {
        ItemStack itemStack = new ItemStack(Material.NAUTILUS_SHELL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§6" + plugin.getName());
        meta.setCustomModelData(plugin.getModeldata());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void sendLink(Player player, MyPlugin plugin) {
        sendLink(player, "[Click here to view §6§l" + plugin.getName() + "§a§l on SpigotMC]", plugin.getLink(), "§aClick to view §6" + plugin.getName() + "§a on SpigotMC");
    }

    public static void sendLink(Player player, String text, String link, String hover) {
        player.closeInventory();
        TextComponent message = new TextComponent(
                "§7§l===========================================\n" +
                        "§a§l" + text + "\n" +
                        "§7§l===========================================");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        player.spigot().sendMessage(message);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUIHolder)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMainMenuClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUIHolder holder)) return;
        if (!holder.context.equals("pluginlist")) return;
        if (holder.getPlugin() != null) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        MyPlugin plugin = main.getPlugin(clickedItem);
        Player player = (Player) event.getWhoClicked();

        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasCustomModelData() && plugin == null) {
            switch (clickedItem.getItemMeta().getCustomModelData()) {
                case 1001 -> sendLink(player, "§b§lClick here to join my §b§lDiscord §b§lserver!", "https://discord.jeff-media.com", "§bClick to join my Discord server");
                case 1002 -> player.setGameMode(GameMode.SURVIVAL);
                case 1003 -> player.setGameMode(GameMode.CREATIVE);
            }
            player.closeInventory();
            return;
        }

        if (plugin == null) return;
        showPluginMenu(player, plugin);
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUIHolder holder)) return;
        if (holder.getPlugin() == null) return;
        MyPlugin plugin = holder.plugin;
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (!clickedItem.hasItemMeta()) return;
        if (!clickedItem.getItemMeta().hasCustomModelData()) return;
        int modeldata = clickedItem.getItemMeta().getCustomModelData();
        event.setCancelled(true);
        switch (holder.context) {
            case "plugin":
                switch (modeldata) {
                    case 101 -> sendLink(player, plugin);
                    case 102 -> tryPlugin(player, plugin);
                }
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public record GUIHolder(String context,MyPlugin plugin) implements InventoryHolder {

        @NotNull
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}
