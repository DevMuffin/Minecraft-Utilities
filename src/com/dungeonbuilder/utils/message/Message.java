package com.dungeonbuilder.utils.message;

import java.awt.Color;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.dungeonbuilder.utils.logger.Logs;
import com.dungeonbuilder.utils.serialization.info.Dungeon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class Message {

//	@formatter:off
	public static final ChatColor REGULAR_COLOR = ChatColor.GRAY,
			ACCENT_COLOR = ChatColor.of(new Color(0xFF33FFCC)),
			ERROR_COLOR = ChatColor.RED,
			ERROR_ACCENT_COLOR = ChatColor.DARK_RED;
	
	public static final TextColor REGULAR_COLOR_COMPONENT = NamedTextColor.GRAY,
			ACCENT_COLOR_COMPONENT = TextColor.color(0xFF33FFCC);
//	@formatter:on

	public static TextComponent PREFIX = Component.text("[").color(NamedTextColor.GRAY)
			.append(Component.text("DungeonBuilder").color(TextColor.color(0xFF33FFCC)))
			.append(Component.text("] ").color(NamedTextColor.GRAY)),
			ERROR_PREFIX = Component.text("[").color(NamedTextColor.RED)
					.append(Component.text("DungeonBuilder").color(NamedTextColor.DARK_RED))
					.append(Component.text("] ").color(NamedTextColor.RED));

	public static TextComponent FORMAT_MESSAGE(String text, ChatColor regularColor, ChatColor accentColor) {
		String[] lines = text.split("\n");
		if (lines.length == 1) {
			return LegacyComponentSerializer.legacySection().deserialize(
					regularColor
							+ text.replaceAll("&a", accentColor.toString()).replaceAll("&r", regularColor.toString()));
		} else {
			Component ret = Component.text("");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				Logs.debug(line);
				Logs.debug(FORMAT_MESSAGE(line, regularColor, accentColor).content());
				ret = ret.append(FORMAT_MESSAGE(line, regularColor, accentColor));
				if (i != lines.length - 1) {
					ret = ret.appendNewline();
				}
			}
			return (TextComponent) ret;
		}
	}

	public enum MessageType {
		MESSAGE, RAW, ERROR;
	}

	public enum MessageDestination {
		PLAYER, DUNGEON;
	}

	// public static Message m(String text) {
	// return new Message(text, MessageType.MESSAGE);
	// }

	public static String stylize(String... texts) {
		String styled = "";
		for (int i = 0; i < texts.length; i++) {
			if (i % 2 != 0) {
				styled += "&a";
			} else if (i > 0) {
				styled += "&r";
			}
			styled += texts[i] + (i == texts.length - 1 ? "" : " ");
		}
		return styled;
	}

	public static Message m(String... texts) {
		return new Message(stylize(texts), MessageType.MESSAGE);
	}

	public static Message rm(String text) {
		return new Message(text, MessageType.RAW);
	}

	public static Message e(String... texts) {
		return new Message(stylize(texts), MessageType.ERROR);
	}

	public static Message noAccess() {
		return new Message("You &adon't&r have access!", MessageType.ERROR);
	}

	public static Message errorAWS() {
		return new Message("Error contacting our servers, &aplease try again later&r!", MessageType.ERROR);
	}

	public static Message errorNoPerms() {
		return new Message("You &adon't&r have permission to do that!", MessageType.ERROR);
	}

	public static record SoundInfo(Sound sound, float volume, float pitch) {
	}

	private String text;
	private MessageType type;
	private SoundInfo sound;

	public Message(String text, MessageType type) {
		this.text = text;
		this.type = type;
	}

	public Message withSound(SoundInfo sound) {
		this.sound = sound;
		return this;
	}

	public TextComponent component(MessageDestination destination) {
		ChatColor regularColor = REGULAR_COLOR, accentColor = ACCENT_COLOR;
		if (destination.equals(MessageDestination.DUNGEON)) {
			regularColor = ChatColor.DARK_PURPLE;
			accentColor = ChatColor.LIGHT_PURPLE;
		}
		switch (this.type) {
			case MESSAGE:
				return FORMAT_MESSAGE(this.text, regularColor, accentColor);
			case RAW:
				return LegacyComponentSerializer.legacySection().deserialize(regularColor + this.text);
			case ERROR:
				return FORMAT_MESSAGE(this.text, ERROR_COLOR, ERROR_ACCENT_COLOR);
			default:
				return FORMAT_MESSAGE(this.text, regularColor, accentColor);
		}
	}

	private TextComponent prefix() {
		switch (this.type) {
			case ERROR:
				return ERROR_PREFIX;
			// $CASES-OMITTED$
			default:
				return PREFIX;
		}
	}

	public void send(Player player) {
		player.sendMessage(this.prefix().append(this.component(MessageDestination.PLAYER)));
		if (this.sound != null) {
			player.playSound(player.getLocation(), this.sound.sound, this.sound.volume, this.sound.pitch);
		}
	}

	public void send(UUID playerId) {
		this.send(Bukkit.getPlayer(playerId));
	}

	public void send(Dungeon dungeon) {
		for (UUID uuid : dungeon.getMembers()) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			if (!op.isOnline()) {
				continue;
			}
			Player player = (Player) op;
			player.sendMessage(Component.text("[" + dungeon.getNameOrElseId() + "] ").color(NamedTextColor.DARK_PURPLE)
					.append(this.component(MessageDestination.DUNGEON)));
			if (this.sound != null) {
				player.playSound(player.getLocation(), this.sound.sound, this.sound.volume, this.sound.pitch);
			}
		}
	}

	public void sendActionbar(Player player) {
		player.sendActionBar(this.component(MessageDestination.PLAYER));
	}

	public void sendAndActionbar(Player player) {
		this.send(player);
		this.sendActionbar(player);
	}
}
