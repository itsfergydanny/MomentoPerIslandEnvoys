package ca.dannyferguson.momentoperislandenvoys.listeners;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;
import ca.dannyferguson.momentoperislandenvoys.utils.Chat;
import ca.dannyferguson.momentoperislandenvoys.utils.Logger;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

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

        Location loc = block.getLocation().clone().add(0.5, 0, 0.5);

        if (!plugin.getLOCATIONS().containsKey(loc)) {
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

        List<ArmorStand> armorStands = plugin.getLOCATIONS().remove(loc);
        if (armorStands != null) {
            armorStands.forEach(Entity::remove);
            String command = plugin.getRandomCommand().replace("<player>", player.getName());
            if (command.startsWith("give ")) {
                String amount = command.split(" ")[3];
                String itemName = command.split(" ")[2].replace("_", " ");
                player.sendMessage(Chat.format("&aYou have found: &f" + amount + "x " + itemName));
            }
            Logger.info("Dispatching command: " + command);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
