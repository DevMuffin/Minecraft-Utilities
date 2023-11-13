package com.dungeonbuilder.utils.item;

import java.util.Locale;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import com.dungeonbuilder.utils.entity.EntityUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class EnumDisplayName {

	public static String of(String enumName) {
		return WordUtils.capitalize(enumName.replaceAll("_", " ").toLowerCase(Locale.US));
	}

	public static String of(Enum<?> en) {
		if (en instanceof EntityType) {
			return EntityUtils.getDisplayEntityNameFromType((EntityType) en);
		}
		return EnumDisplayName.of(en.name());
	}

	/**
	 * @return proper name of the PotionEffectType
	 */
	public static String ofPotionEffectType(PotionEffectType effectType) {
		if (effectType.equals(PotionEffectType.SLOW)) {
			return "Slowness";
		}
		String ret = EnumDisplayName.of(effectType.getName());
		if (effectType.equals(PotionEffectType.FAST_DIGGING)) {
			ret += " (Haste)";
		}
		if (effectType.equals(PotionEffectType.SLOW_DIGGING)) {
			ret += " (Mining Fatigue)";
		}
		return ret;
	}

	/**
	 * @return properly colored name of the PotionEffectType
	 */
	public static Component ofPotionEffectTypeColored(PotionEffectType effectType) {
		return Component.text(ofPotionEffectType(effectType)).color(TextColor.color(effectType.getColor().asRGB()));
	}
}
