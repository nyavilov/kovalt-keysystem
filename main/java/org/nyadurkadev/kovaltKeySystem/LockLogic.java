package org.nyadurkadev.kovaltKeySystem;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.UUID;

public class LockLogic implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack playerItem = event.getItem();

        // для обычного ключа, т.е проверка такая, для связки дальше
        String keyInHandID = null;
        if (playerItem != null && playerItem.hasItemMeta()) {
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "key_id");
            keyInHandID = playerItem.getItemMeta()
                    .getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }

        // для связки
        String keyChainIDs = null;
        if (playerItem != null && playerItem.hasItemMeta()) {
            NamespacedKey chainKey = new NamespacedKey(Main.getInstance(), "key_ids");
            keyChainIDs = playerItem.getItemMeta()
                    .getPersistentDataContainer().get(chainKey, PersistentDataType.STRING);
        }

        // запрет на поставку tripwire hook (ключа) и chain (связки) на дверь
        if (keyInHandID != null || keyChainIDs != null) {
            event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
            player.updateInventory();
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        if (clickedBlock.getBlockData() instanceof Door
                || clickedBlock.getBlockData() instanceof TrapDoor) {

            Location loc = clickedBlock.getLocation();

            if (clickedBlock.getBlockData() instanceof Door) {
                Door door = (Door) clickedBlock.getBlockData();
                if (door.getHalf() == Bisected.Half.TOP) {
                    loc.subtract(0, 1, 0);
                }
            }

            String requiredID = Main.getLogic().getKeyID(loc);
            UUID owner = Main.getLogic().getOwner(loc);

            // это проверка на замок!
            if (requiredID == null) {
                if ("LOCK".equals(keyInHandID)
                        && player.isSneaking()) {
                    String newID = UUID.randomUUID().toString().substring(0, 6);
                    Main.getLogic().lockDoor(loc, newID, player.getUniqueId());

                    ItemStack lockItem = player.getInventory().getItemInMainHand();
                    lockItem.setAmount(lockItem.getAmount() - 1);
                    event.setCancelled(true);

                    player.sendActionBar("§aЗамок повешен на дверь!");
                    player.getWorld().playSound(player.getLocation(),
                            Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
                }
            } else { // это проверка на ключ!
                if (player.isSneaking()
                        && "EMPTY".equals(keyInHandID)) {

                    if (owner != null
                            && !player.getUniqueId().equals(owner)) {
                        player.sendActionBar("§cТолько владелец может делать копии ключей!");
                        player.getWorld().playSound(player.getLocation(),
                                Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        event.setCancelled(true);
                        return;
                    }

                    if (playerItem.getAmount() > 1) {
                        // если в руке пачка ключей, то забираем один пустой
                        playerItem.setAmount(playerItem.getAmount() - 1);

                        ItemStack keyItem = playerItem.clone();
                        keyItem.setAmount(1);

                        ItemMeta meta = keyItem.getItemMeta();
                        NamespacedKey key = new NamespacedKey(Main.getInstance(), "key_id");
                        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, requiredID);
                        meta.setCustomModelData(91);

                        meta.setDisplayName("§6Дверной ключ");
                        meta.setLore(Arrays.asList(
                                "",
                                "§fНажмите §6«ПКМ» §fпо двери",
                                "§fдля открытия.",
                                "",
                                "§fID ключа: §7" + requiredID,
                                ""));
                        keyItem.setItemMeta(meta);

                        player.getInventory().addItem(keyItem);
                        player.sendActionBar("§aКлюч успешно создан!");
                    } else {
                        // если в руке всего один, то просто превращаем его в рабочий
                        NamespacedKey key = new NamespacedKey(Main.getInstance(), "key_id");
                        ItemMeta meta = playerItem.getItemMeta();
                        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, requiredID);
                        meta.setCustomModelData(91);

                        meta.setDisplayName("§6Дверной ключ");
                        meta.setLore(Arrays.asList(
                                "",
                                "§fНажмите §6«ПКМ» §fпо двери",
                                "§fдля открытия.",
                                "",
                                "§fID ключа: §7" + requiredID,
                                ""));
                        playerItem.setItemMeta(meta);
                        player.sendActionBar("§aКлюч успешно создан!");
                    }

                    player.updateInventory();
                    player.getWorld().playSound(player.getLocation(),
                            Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
                    return;
                }

                boolean ifSingleKey = keyInHandID != null && keyInHandID.equals(requiredID);
                boolean ifKeyInChain = keyChainIDs != null && keyChainIDs.contains(requiredID);


                if (ifSingleKey || ifKeyInChain) { // в ручке ключик
                    event.setCancelled(false);
                } else { // если в руке нет ключа и т.п
                    event.setCancelled(true);
                    player.sendActionBar("§cЗаперто!");
                    player.getWorld().playSound(player.getLocation(),
                            Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
            }
        }
    }
}
