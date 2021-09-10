package ca.dannyferguson.momentoperislandenvoys.utils;

import ca.dannyferguson.momentoperislandenvoys.MomentoPerIslandEnvoys;

public class Executor {
    private static MomentoPerIslandEnvoys plugin;

    public static void init(MomentoPerIslandEnvoys plugin) {
        Executor.plugin = plugin;
    }

    public static void sync(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public static void syncLater(Runnable runnable, int delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static void async(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void asyncLater(Runnable runnable, int delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public static void syncRepeating(Runnable runnable, int delay) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, delay);
    }

    public static void asyncRepeating(Runnable runnable, int delay) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, delay);
    }

    public static boolean isMainThread() {
        return plugin.getServer().isPrimaryThread();
    }
}
