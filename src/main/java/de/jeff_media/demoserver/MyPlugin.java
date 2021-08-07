package de.jeff_media.demoserver;

import com.google.common.base.Enums;
import de.jeff_media.jefflib.LocationUtils;
import de.jeff_media.jefflib.RandomUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public record MyPlugin(String name, int modeldata, List<ItemStack> items, Location location, String link, List<String> message, boolean shuffleItems, GameMode gameMode, int randomItems) {

    public static MyPlugin getFromConfig(ConfigurationSection section) {
        String name = section.getName();
        int modeldata = section.getInt("modeldata");
        List<ItemStack> items;
        if (section.isList("items")) {
            //noinspection unchecked
            items = (List<ItemStack>) section.getList("items");
        } else {
            items = new ArrayList<>();
        }
        Location location = null;
        if (section.isConfigurationSection("location")) {
            location = LocationUtils.getLocationFromSection(section.getConfigurationSection("location"), null);
        }
        return new MyPlugin(name,
                modeldata,
                items,
                location,
                section.getString("link"),
                section.getStringList("message"),
                section.getBoolean("shuffle-items", false),
                Enums.getIfPresent(GameMode.class, section.getString("gamemode", "")).orNull(),
                section.getInt("give-random-items", 0));
    }

    public List<ItemStack> getItems() {
        if (shuffleItems) {
            Collections.shuffle(items);
        }

        if (randomItems > 0) {
            List<ItemStack> newItems = new ArrayList<>();
            for (int i = 0; i <= 20; i++) {
                Material mat = null;
                while (mat == null) {
                    Material mat2 = Material.values()[RandomUtils.getInt(0, Material.values().length)];
                    if (mat2.isBlock() || mat2.isItem()) {
                        mat = mat2;
                    }
                }
                ItemStack item = new ItemStack(mat, RandomUtils.getInt(0, mat.getMaxStackSize()));
                newItems.add(item);
            }
            items.addAll(newItems);
        }

        return items;
    }
}
