package ca.dannyferguson.momentoperislandenvoys.commands;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;
import ca.dannyferguson.momentoperislandenvoys.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestEnvoyCommand implements CommandExecutor {
    private MomentoPerIslandEnvoys plugin;

    public TestEnvoyCommand(MomentoPerIslandEnvoys plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("momentoperislandenvoys.admin")) {
            sender.sendMessage(Chat.format("&cNo permission."));
            return true;
        }

        plugin.startEnvoy();
        sender.sendMessage(Chat.format("&aStarted envoy!"));

        return true;
    }
}
