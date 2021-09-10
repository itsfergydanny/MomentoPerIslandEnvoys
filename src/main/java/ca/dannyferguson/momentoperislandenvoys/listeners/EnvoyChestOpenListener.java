package ca.dannyferguson.momentoperislandenvoys.listeners;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;
import ca.dannyferguson.momentoperislandenvoys.objects.FixedLocation;
import ca.dannyferguson.momentoperislandenvoys.utils.Chat;
import ca.dannyferguson.momentoperislandenvoys.utils.Logger;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnvoyChestOpenListener implements Listener {
    private MomentoPerIslandEnvoys plugin;

    public EnvoyChestOpenListener(MomentoPerIslandEnvoys plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onOpenEnvoyChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.CHEST) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Location loc = block.getLocation();
        player.sendMessage("loc=" + FixedLocation.fromLocation(loc));
        player.sendMessage("possible locs =");
        plugin.getLOCATIONS().keySet().forEach(loci -> {
            player.sendMessage(loci.toString());
        });

        if (!plugin.getLOCATIONS().containsKey(FixedLocation.fromLocation(loc))) {
            player.sendMessage("doesnt contain loc");
            return;
        }

        Island island = SuperiorSkyblockAPI.getIslandAt(loc);
        if (island == null) {
            return;
        }

        if (!island.getIslandMembers(true).contains(SuperiorSkyblockAPI.getPlayer(player))) {
            player.sendMessage(Chat.format("&cOnly island members can open envoy drops on their island!"));
            return;
        }

        block.setType(Material.AIR);

        ArmorStand armorStand = plugin.getLOCATIONS().remove(FixedLocation.fromLocation(loc));
        if (armorStand != null) {
            armorStand.remove();
            String command = plugin.getRandomCommand().replace("<player>", player.getName());
            Logger.info("Dispatching command: " + command);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
