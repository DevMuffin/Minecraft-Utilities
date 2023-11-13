package com.dungeonbuilder.utils.entity.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class DisplayName {

	public static TextComponent asComponent(Player player) {
		return (TextComponent) player.displayName();
	}

	public static String asString(Player player) {
		return asComponent(player).content();
	}

	public static String asString(OfflinePlayer offlinePlayer) {
		return offlinePlayer.getName();
	}

	public static TextComponent asComponent(OfflinePlayer offlinePlayer) {
		return Component.text(asString(offlinePlayer));
	}
}
