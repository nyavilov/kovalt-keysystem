package org.nyadurkadev.kovaltKeySystem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class KeyLogic {
    private HashMap<Location, String> locationHashMap = new HashMap<>();
    private HashMap<Location, UUID> doorOwners = new HashMap<>();

    public void save(FileConfiguration config) {
        config.set("doors", null);

        for (Map.Entry<Location, String> entry : locationHashMap.entrySet()) {
            Location loc = entry.getKey();
            String id = entry.getValue();
            UUID owner = doorOwners.get(loc);

            String stringLoc = locToString(loc);

            config.set("doors." + stringLoc + ".id", id);
            config.set("doors." + stringLoc + ".owner", owner.toString());
        }
    }

    public void load(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("doors");

        if (section != null) {
            for (String stringLoc : section.getKeys(false)) {
                Location loc = stringToLoc(stringLoc);
                String id = section.getString(stringLoc + ".id");
                String ownerUUID = section.getString(stringLoc + ".owner");

                locationHashMap.put(loc, id);
                doorOwners.put(loc, UUID.fromString(ownerUUID));
            }
        }
    }

    public UUID getOwner(Location loc) {
        Location blockLoc = loc.getBlock().getLocation();
        return doorOwners.get(blockLoc);
    }

    private String locToString(Location loc) {
       String world = loc.getWorld().getName();
       int coordX = loc.getBlockX();
       int coordY = loc.getBlockY();
       int coordZ = loc.getBlockZ();

       String locString = (world + "," + coordX + "," + coordY + "," + coordZ);

       return locString;
    }

    private Location stringToLoc(String s) {
        String[] parts = s.split(",");

        World world = Bukkit.getWorld(parts[0]);
        int coordX = Integer.parseInt(parts[1]);
        int coordY = Integer.parseInt(parts[2]);
        int coordZ = Integer.parseInt(parts[3]);

        return new Location(world,coordX, coordY, coordZ);
    }

    public void lockDoor(Location loc, String id, UUID owner) {
        Location blockLoc = loc.getBlock().getLocation();
        locationHashMap.put(blockLoc, id);
        doorOwners.put(blockLoc, owner);
    }

    public boolean isLocked(Location loc) {
        Location blockLoc = loc.getBlock().getLocation();
        return locationHashMap.containsKey(blockLoc);
    }

    public String getKeyID(Location loc) {
        Location blockLoc = loc.getBlock().getLocation();
        return locationHashMap.get(blockLoc);
    }
}
