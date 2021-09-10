package ca.dannyferguson.momentoperislandenvoys.placeholderapi;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;

import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIExtension extends PlaceholderExpansion {
    private final MomentoPerIslandEnvoys plugin;

    public PAPIExtension(MomentoPerIslandEnvoys plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "fergydanny";
    }

    @Override
    public String getIdentifier() {
        return "momentoenvoys";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("countdown")){
            long msUntil = plugin.getNextEnvoy() - System.currentTimeMillis();
            if (msUntil < 0) {
                return "Now!";
            }

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

            return str.toString();
        }

        if(params.equalsIgnoreCase("placeholder2")) {
            return plugin.getConfig().getString("placeholders.placeholder2", "default2");
        }

        return null; // Placeholder is unknown by the Expansion
    }
}