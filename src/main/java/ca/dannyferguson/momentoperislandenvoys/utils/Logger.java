package ca.dannyferguson.momentoperislandenvoys.utils;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;

import java.util.logging.Level;

public class Logger {
    private static MomentoPerIslandEnvoys plugin;

    public static void init(MomentoPerIslandEnvoys plugin) {
        Logger.plugin = plugin;
    }

    public static void info(String msg) {
        plugin.getLogger().info(msg);
    }

    public static void log(String msg, Level level) {
        plugin.getLogger().log(level, msg);
    }
}
