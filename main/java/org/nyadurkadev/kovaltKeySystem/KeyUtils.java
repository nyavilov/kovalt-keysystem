package org.nyadurkadev.kovaltKeySystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KeyUtils implements CommandExecutor {

    public void registerRecipes() {
        ItemStack lockItem = createLock();

        NamespacedKey keyLock = new NamespacedKey(Main.getInstance(), "lock_recipe");

        ShapedRecipe lockRecipe = new ShapedRecipe(keyLock, lockItem);
        lockRecipe.shape("CGC", "CDC", " G ");
        lockRecipe.setIngredient('C', Material.COPPER_INGOT);
        lockRecipe.setIngredient('G', Material.GOLD_INGOT);
        lockRecipe.setIngredient('D', Material.DIAMOND);

        Bukkit.addRecipe(lockRecipe);

        // крафт ключа
        ItemStack keyItem = createKey("EMPTY");

        NamespacedKey keyKey = new NamespacedKey(Main.getInstance(), "key_recipe");

        ShapedRecipe keyRecipe = new ShapedRecipe(keyKey, keyItem);
        keyRecipe.shape(" C ", " G ", " C ");
        keyRecipe.setIngredient('C', Material.COPPER_INGOT);
        keyRecipe.setIngredient('G', Material.GOLD_INGOT);

        Bukkit.addRecipe(keyRecipe);

        // крафт связки
        ItemStack chainItem = createKeyChain();

        NamespacedKey chainKey = new NamespacedKey(Main.getInstance(), "chain_recipe");

        ShapedRecipe chainRecipe = new ShapedRecipe(chainKey, chainItem);
        chainRecipe.shape(" H ", " C ", " H ");
        chainRecipe.setIngredient('H', Material.IRON_CHAIN);
        chainRecipe.setIngredient('C', Material.COPPER_INGOT);

        Bukkit.addRecipe(chainRecipe);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cКоманда для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equals("getkey")) {
            if (args.length == 0) {
                ItemStack key = createKey("EMPTY");
                player.getInventory().addItem(key);
                return true;
            } else {
                ItemStack key = createKey(args[0]);
                player.getInventory().addItem(key);
            }
            return true;
        }

        if (command.getName().equals("getlock")) {
            player.getInventory().addItem(createLock());
        }

        return true;
    }

    // создание ключа
    public ItemStack createKey(String id) {
        ItemStack keyItem = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta keyItemMeta = keyItem.getItemMeta();

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "key_id");
        keyItemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);

        keyItemMeta.setDisplayName("§6Ключ-пустышка");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fДля активации ключа");
        lore.add("§fнажмите по двери с замком §6«ПКМ» + «Shift»");
        lore.add("§fи он успешно привяжется!");
        lore.add("");
        if ("EMPTY".equals(id)) {
            lore.add("§fID ключа: §7EMPTY");
        } else {
            lore.add("§fID ключа: §7" + id);
        }
        lore.add("");
        keyItemMeta.setLore(lore);

        keyItem.setItemMeta(keyItemMeta);

        return keyItem;
    }

    public ItemStack createKeyChain() {
        ItemStack keyChain = new ItemStack(Material.IRON_CHAIN);
        ItemMeta keyChainMeta = keyChain.getItemMeta();

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "key_ids");
        keyChainMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");

        keyChainMeta.setDisplayName("§6Связка ключей");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fЭто ваша личная связка ключей");
        lore.add("§fНажмите по двери §6«ПКМ»");
        lore.add("§fИ если ключ подходит - вы войдете");
        lore.add("");
        lore.add("§c⚠ Связку не развязать!");
        keyChainMeta.setLore(lore);

        keyChain.setItemMeta(keyChainMeta);

        return keyChain;
    }

    // создание замка
    public ItemStack createLock() {
        ItemStack lockItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta lockMeta = lockItem.getItemMeta();

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "key_id");
        lockMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "LOCK");

        lockMeta.setDisplayName("§7Дверной замок");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fНавесьте замок на дверь");
        lore.add("§fНажав §6«ПКМ» + «Shift»");
        lore.add("");
        lockMeta.setLore(lore);

        lockItem.setItemMeta(lockMeta);

        return lockItem;
    }
}
