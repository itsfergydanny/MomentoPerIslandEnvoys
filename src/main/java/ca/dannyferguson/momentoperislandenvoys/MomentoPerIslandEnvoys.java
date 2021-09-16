package ca.dannyferguson.momentoperislandenvoys;

import ca.dannyferguson.momentoperislandenvoys.commands.EnvoyCommand;
import ca.dannyferguson.momentoperislandenvoys.commands.TestEnvoyCommand;
import ca.dannyferguson.momentoperislandenvoys.listeners.EnvoyChestOpenListener;
import ca.dannyferguson.momentoperislandenvoys.placeholderapi.PAPIExtension;
import ca.dannyferguson.momentoperislandenvoys.utils.Chat;
import ca.dannyferguson.momentoperislandenvoys.utils.Executor;
import ca.dannyferguson.momentoperislandenvoys.utils.Logger;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class MomentoPerIslandEnvoys extends JavaPlugin {
    private Map<Double, String> ODDS;
    private Map<Location, List<ArmorStand>> LOCATIONS;
    private List<String> COMMANDS;
    private long nextEnvoy;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        nextEnvoy = getConfig().getLong("next", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60));

        Executor.init(this);
        Logger.init(this);

        ODDS = new ConcurrentHashMap<>();
        LOCATIONS = new ConcurrentHashMap<>();
        COMMANDS = new ArrayList<>();

        loadPossibleDrops();

        for (Map.Entry<Double, String> entry : ODDS.entrySet()) {
            for (int i = 0; i < entry.getKey(); i++) {
                COMMANDS.add(entry.getValue());
            }
        }

        getCommand("testenvoy").setExecutor(new TestEnvoyCommand(this));
        getCommand("envoy").setExecutor(new EnvoyCommand(this));

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EnvoyChestOpenListener(this), this);

        new PAPIExtension(this).register();

        Executor.syncRepeating(() -> {
            if ((nextEnvoy - System.currentTimeMillis()) < 0) {
                startEnvoy();
                nextEnvoy = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60);
            }
        }, 100);
    }

    private void loadPossibleDrops() {
        for (String line : getConfig().getStringList("commands")) {
            String[] args = line.split(":");
            if (args.length != 2) {
                continue;
            }
            double chance;
            try {
                chance = Double.parseDouble(args[0]);
                String command = args[1];
                ODDS.put(chance, command);
                Logger.info("Added command \"" + command + "\" with chance " + chance + " to possible drops!");
            } catch (NumberFormatException ignore) {
                Logger.info("Invalid command defined in line \"" + line + "\"");
            }
        }
    }

    public void startEnvoy() {
        long now = System.currentTimeMillis();

        Logger.info("Starting envoy!");
        Logger.info("Clearing any past envoys..");
        stopEnvoy();

        Set<Island> islands = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
            Island island = superiorPlayer.getIsland();
            if (island != null) {
                islands.add(island);
            }
        }

        Logger.info("Loaded " + islands.size() + " islands to send envoys to..");

        for (Island island : islands) {
            List<Location> locs = new ArrayList<>();
            int amountToDrop = ThreadLocalRandom.current().nextInt(1, 6);
            Location center = island.getCenter(World.Environment.NORMAL);
            for (int i = 0; i < amountToDrop; i++) {
                Location loc = getRandomLocationOnIsland(center);
                if (loc != null) {
                    //spawn chest
                    spawnBox(loc);
                    locs.add(loc);
                }
            }
            if (locs.size() > 0) {
                StringBuilder str = new StringBuilder("&4&lEnvoy Event\n&c").append(locs.size()).append(" loot boxes have spawned on your island at the following coordinates:");
                for (Location loc : locs) {
                    str.append("\n&f- ").append(formatLocation(loc));
                }
                island.sendMessage(Chat.format(str.toString()));
                island.sendTitle(Chat.format("&4Envoy event"), Chat.format("&cHead to your island!"), 20, 20, 20);
            }
        }

        Logger.info("Envoy event performed in " + (System.currentTimeMillis() - now) + " ms!");
    }

    private void spawnBox(Location loc) {
        loc.getBlock().setType(Material.CHEST);

        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ()), EntityType.ARMOR_STAND);
        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setInvulnerable(true);
        as.setCustomName(Chat.format("&c&lLOOT BOX"));
        as.setCustomNameVisible(true);
        as.setVisible(false);

        ArmorStand as2 = (ArmorStand) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1.3, loc.getZ()), EntityType.ARMOR_STAND);
        as2.setGravity(false);
        as2.setCanPickupItems(false);
        as2.setInvulnerable(true);
        as2.setCustomName(Chat.format("&eClick to open!"));
        as2.setCustomNameVisible(true);
        as2.setVisible(false);

        List<ArmorStand> armorStands = new ArrayList<>();
        armorStands.add(as);
        armorStands.add(as2);

        loc.getWorld().strikeLightningEffect(loc);

        LOCATIONS.put(loc, armorStands);
    }

    public String formatLocation(Location location) {
        return "x: " + location.getX() + ", y: " + location.getY() + ", z: " + location.getZ();
    }

    private Location getRandomLocationOnIsland(Location center) {
        int randomX = ThreadLocalRandom.current().nextInt(40);
        int randomY = ThreadLocalRandom.current().nextInt(40);
        int randomZ = ThreadLocalRandom.current().nextInt(30);
        for (int j = 0; j < 5; j ++) {
            Location random = center.clone().add(randomX, randomY, randomZ);
            if (random.getBlock().isEmpty() && !LOCATIONS.containsKey(random)) {
                return random;
            }
        }
        return null;
    }

    public void stopEnvoy() {
        int removed = 0;
        for (Location loc : LOCATIONS.keySet()) {
            loc.getBlock().setType(Material.AIR, false);
            LOCATIONS.get(loc).forEach(armorStand -> {
                if (armorStand != null) {
                    armorStand.remove();
                }
            });
            LOCATIONS.get(loc).forEach(Entity::remove);
            removed++;
        }
        LOCATIONS.clear();
        Logger.info("Removed " + removed + " existing envoys.");

        Logger.info("Clearing residual holograms..");
        int count = 0;
        World world = Bukkit.getWorld("SuperiorWorld");
        if (world != null) {
            for(Entity entity : world.getEntities()) {
                if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
                    continue;
                }
                if (!entity.getName().equals(Chat.format("&c&lLOOT BOX")) && !entity.getName().equals(Chat.format("&eClick to open!"))) {
                    continue;
                }
                entity.remove();
                count++;
            }
        }
        Logger.info("Cleared " + count + " residual holograms!");
    }

    @Override
    public void onDisable() {
        stopEnvoy();
        getConfig().set("next", nextEnvoy);
        saveConfig();
    }

    public String getRandomCommand() {
        return COMMANDS.get(ThreadLocalRandom.current().nextInt(0, COMMANDS.size()));
    }

    public Map<Location, List<ArmorStand>> getLOCATIONS() {
        return LOCATIONS;
    }

    public long getNextEnvoy() {
        return nextEnvoy;
    }
}
