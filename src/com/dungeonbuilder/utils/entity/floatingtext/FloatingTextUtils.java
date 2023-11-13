package com.dungeonbuilder.utils.entity.floatingtext;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class FloatingTextUtils {

	private static final HashMap<Integer, ArmorStand> FLOATING_TEXTS = new HashMap<Integer, ArmorStand>();

	public static int createFloatingText(String text, Location loc) {
//		Add (0.5, 0, 0.5) so the center of the text is in the center of the block.
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5, 0, 0.5),
				EntityType.ARMOR_STAND);
//		Specify the armor stand as a marker so you can place blocks and punch through the stand (small hit box).
//		Marker also moves the custom name to the base plate of the stand.
		stand.setMarker(true);
		stand.setVisible(false);
		stand.setCustomName(text);
		stand.setCustomNameVisible(true);
		int standId = stand.getEntityId();
		FLOATING_TEXTS.put(standId, stand);
		return standId;
	}

	public static void removeFloatingText(int standId) {
		ArmorStand stand = FLOATING_TEXTS.remove(standId);
		if (stand != null) {
			stand.remove();
		}
	}

	public static void removeAllFloatingTexts() {
		FLOATING_TEXTS.values().forEach(stand -> stand.remove());
		FLOATING_TEXTS.clear();
	}
}
