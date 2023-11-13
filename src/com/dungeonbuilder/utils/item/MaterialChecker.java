package com.dungeonbuilder.utils.item;

import org.bukkit.Material;

public class MaterialChecker {

	public static boolean isPressurePlate(Material material) {
		return material.name().endsWith("PRESSURE_PLATE");
	}

	public static boolean isBanner(Material material) {
		return material.name().endsWith("BANNER");
	}
}
