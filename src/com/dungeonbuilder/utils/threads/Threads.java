package com.dungeonbuilder.utils.threads;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.dungeonbuilder.utils.UtilsPlugin;

public class Threads {

	public static void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(UtilsPlugin.INSTANCE, runnable);
	}

	public static void runAsyncLater(Runnable runnable, long delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(UtilsPlugin.INSTANCE, runnable, delay);
	}

//	Use BukkitRunnable to allow cancel() within runnable to actually end this repeating task.
	public static void runAsyncRepeating(BukkitRunnable runnable, long delay, long period) {
		runnable.runTaskTimerAsynchronously(UtilsPlugin.INSTANCE, delay, period);
	}

	public static void runMain(Runnable runnable) {
		Bukkit.getScheduler().runTask(UtilsPlugin.INSTANCE, runnable);
	}

	public static void runMainLater(Runnable runnable, long delay) {
		Bukkit.getScheduler().runTaskLater(UtilsPlugin.INSTANCE, runnable, delay);
	}

//	Use BukkitRunnable to allow cancel() within runnable to actually end this repeating task.
	public static void runMainRepeating(BukkitRunnable runnable, long delay, long period) {
		runnable.runTaskTimer(UtilsPlugin.INSTANCE, delay, period);
	}
}
