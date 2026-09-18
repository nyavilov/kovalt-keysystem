package org.nyadurkadev.kovaltKeySystem;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ChainLogic implements Listener {

    @EventHandler
    public void ChainInteract(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        Player player = (Player) event.getWhoClicked();

        if (currentItem == null || cursorItem == null) return;
        if (currentItem.getType().equals(Material.AIR) ||
                cursorItem.getType().equals(Material.AIR)) return;

        NamespacedKey currentItemKey = new NamespacedKey(Main.getInstance(), "key_ids");
        NamespacedKey cursorItemKey = new NamespacedKey(Main.getInstance(), "key_id");
        String currentItemID = currentItem.getItemMeta()
                .getPersistentDataContainer().get(currentItemKey, PersistentDataType.STRING);
        String cursorItemID = cursorItem.getItemMeta()
                .getPersistentDataContainer().get(cursorItemKey, PersistentDataType.STRING);

        if (currentItemID != null && cursorItemID != null) {
            if (currentItemID.contains(cursorItemID)) {
                player.sendMessage("§cЭтот ключ уже есть в связке!");
                player.getWorld().playSound(player.getLocation(),
                        Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                event.setCancelled(true);
                return;
            }

            String updatedIDs = currentItemID + cursorItemID + ",";
            ItemMeta meta = currentItem.getItemMeta();
            meta.getPersistentDataContainer()
                    .set(currentItemKey, PersistentDataType.STRING, updatedIDs);

            currentItem.setItemMeta(meta);
            player.sendMessage("§aКлюч добавлен в связку!");

            event.setCursor(null);
            event.setCancelled(true);

            player.getWorld().playSound(player.getLocation(),
                    Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
        }
    }
}
