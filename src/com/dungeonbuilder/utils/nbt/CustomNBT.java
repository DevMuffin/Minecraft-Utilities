package com.dungeonbuilder.utils.nbt;

import org.bukkit.NamespacedKey;

import com.dungeonbuilder.utils.UtilsPlugin;

public class CustomNBT {

//	NBT to give an inventory item a unique ID so two inventory items can be exactly identical with separate click functionalities.
//	Without a unique ID, these items might normally overlap in the inventory (showing as x2 instead of two separate items).
//	This also allows us to save time when checking the equality of two inventory items. We can simply check the equality of UUID
//	rather than checking if all item attributes are equal.
	public static NamespacedKey INV_ITEM_UUID_NAMESPACE_KEY;

//	Set to 1 when a custom mob is loaded (to be edited) in a control zone.
	public static NamespacedKey CUSTOM_MOB_EDITABLE_NBT_KEY;
//	Set to the UUID of a custom mob when it is loaded (to be edited) in a control zone.
	public static NamespacedKey CUSTOM_MOB_UUID_KEY;
//	Set to the UUID of a PlaythroughContext when a custom mob is spawned.
	public static NamespacedKey CUSTOM_MOB_PLAYTHROUGH_ID_KEY;
//	Set to 1 when a Playthrough mob has fire thorns.
	public static NamespacedKey CUSTOM_MOB_PLAYTHROUGH_FIRETHORNS;
//	Set to 1 when a Playthrough mob cannot be damaged.
	public static NamespacedKey CUSTOM_MOB_PLAYTHROUGH_UNDAMAGABLE;

//	Contains a list of all drop representations for a custom mob during the play through of a dungeon.
	public static NamespacedKey PLAYTHROUGH_DROPS;

	public static NamespacedKey FAST_MODE;

	public static void init() {
		INV_ITEM_UUID_NAMESPACE_KEY = new NamespacedKey(UtilsPlugin.INSTANCE, "inv-item-uuid");
		CUSTOM_MOB_EDITABLE_NBT_KEY = new NamespacedKey(UtilsPlugin.INSTANCE, "custom-mob-editable");
		CUSTOM_MOB_UUID_KEY = new NamespacedKey(UtilsPlugin.INSTANCE, "custom-mob-uuid");
		CUSTOM_MOB_PLAYTHROUGH_ID_KEY = new NamespacedKey(UtilsPlugin.INSTANCE, "custom-mob-playthrough-uuid");
		PLAYTHROUGH_DROPS = new NamespacedKey(UtilsPlugin.INSTANCE, "playthrough-drops");
		CUSTOM_MOB_PLAYTHROUGH_FIRETHORNS = new NamespacedKey(UtilsPlugin.INSTANCE, "playthrough-firethorns");
		CUSTOM_MOB_PLAYTHROUGH_UNDAMAGABLE = new NamespacedKey(UtilsPlugin.INSTANCE, "playthrough-invincible");
		FAST_MODE = new NamespacedKey(UtilsPlugin.INSTANCE, "builder-game-settings-fastmode");
	}
}
