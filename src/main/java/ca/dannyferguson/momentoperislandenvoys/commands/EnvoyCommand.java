package ca.dannyferguson.momentoperislandenvoys.commands;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;
import ca.dannyferguson.momentoperislandenvoys.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EnvoyCommand implements CommandExecutor {
    private MomentoPerIslandEnvoys plugin;

    public EnvoyCommand(MomentoPerIslandEnvoys plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String time;

        long msUntil = plugin.getNextEnvoy() - System.currentTimeMillis();
        if (msUntil < 0) {
            time = "Now!";
        } else {
            int hours = (int) (msUntil / 3_600_000);
            msUntil -= hours * 3_600_000L;

            int minutes = (int) msUntil / 60_000;
            msUntil -= minutes * 60_000;

            int seconds = (int) msUntil / 1_000;

            StringBuilder str = new StringBuilder();
            if (hours > 0) {
                str.append(hours).append("h ");
            }
            if (minutes > 0) {
                str.append(minutes).append("m ");
            }
            if (seconds > 0) {
                str.append(seconds).append("s");
            }

            time = str.toString();
        }

        sender.sendMessage(Chat.format("&cEnvoys happen on islands every hour which drops loot boxes at random coordinates on your island for you and your team to loot! Next envoy in: &f" + time));

        return true;
    }
}
