package com.dungeonbuilder.utils.item;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.dungeonbuilder.utils.inventory.GUI;
import com.dungeonbuilder.utils.inventory.ModernInventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Inventory item builder.
 * 
 * @author Anthony
 */
public class InventoryItem extends ItemBuilderParamd<InventoryItem> {

	public static InventoryItem awsErrorItem() {
		return new InventoryItem().material(Material.BARRIER)
				.name(Component.text("Error contacting our servers!").color(NamedTextColor.RED))
				.lore(Component.text("Please try again later!").color(NamedTextColor.RED));
	}

	/**
	 * Note if the Inventory is bordered, the slot location index represents the
	 * position in the inner pane of the border. For the absolute position see
	 * {@link #absSlot}.
	 */
	public int slot = -1;

	/**
	 * The absolute position of the item in the Inventory. For the relative position
	 * in the inner pane of a bordered inventory, see {@link #slot}.
	 */
	public int absSlot = -1;

	/**
	 * The page of the Inventory the item is in.
	 */
	public int page = -1;

	public ModernInventory container;

	public Optional<Consumer<InventoryItemClickEvent>> clickOpt = Optional.empty();

	public HashMap<String, Consumer<LiveDataChangeEvent>> liveDataChangeConsumers = new HashMap<>();

	public InventoryItem() {
	}

	public InventoryItem(InventoryItem copy) {
		super(copy);
		this.slot = copy.slot;
		this.absSlot = copy.absSlot;
		this.page = copy.page;
		this.container = copy.container;
		this.clickOpt = copy.clickOpt;
		this.liveDataChangeConsumers = copy.liveDataChangeConsumers;
	}

	/**
	 * Note if there is a border in the inventory, the slot location index will be
	 * normalized to be the inner pane of the border.
	 * 
	 * @param slot - the slot location index of the item in the inventory.
	 * @return
	 */
	@Override
	public InventoryItem slot(int slot) {
		this.slot = slot;
		return this;
	}

//	Non overrides
//	These methods do not override from the ItemBuilderParamd class since they are intended for use by the Inventory container only.
//	This keeps them hidden to the InventoryItem class only.
	/**
	 * This method is designed to be called by the Inventory containing the item.
	 * {@link #absSlot} should be treated as a read-only variable.
	 * 
	 * @param absSlot
	 * @return
	 */
	public InventoryItem absSlot(int absSlot) {
		this.absSlot = absSlot;
		return this;
	}

	/**
	 * This method is designed to be called by the Inventory containing the item.
	 * {@link #page} should be treated as a read-only variable.
	 * 
	 * @param page
	 * @return
	 */
	public InventoryItem page(int page) {
		this.page = page;
		return this;
	}

	/**
	 * Note: the consumed Event will automatically be cancelled by default. Use
	 * {@link InventoryClickEvent#setCancelled(boolean)} on the consumed
	 * {@link InventoryClickEvent} to change this behavior.
	 * 
	 * @param clickConsumer
	 * @return
	 */
	@Override
	public InventoryItem click(Consumer<InventoryItemClickEvent> clickConsumer) {
		this.clickOpt = Optional.of(clickConsumer);
		return this;
	}

	@Override
	public InventoryItem liveDataChange(String key, Consumer<LiveDataChangeEvent> dataChangeConsumer) {
		this.liveDataChangeConsumers.put(key, dataChangeConsumer);
		return this;
	}

	@Override
	public InventoryItem container(ModernInventory container) {
		this.container = container;
		return this;
	}

	public void refresh() {
		this.container.updateItem(this);
	}

	/**
	 * Clear out old consumers from an item if trying to re-use under a different
	 * context. Especially useful after
	 * {@link #setInnerIS(org.bukkit.inventory.ItemStack)}.
	 */
	public void cleanse() {
		this.clickOpt = Optional.empty();
		this.liveDataChangeConsumers.clear();
	}

	public void remove() {
//		Make sure the container is aware and can remove this reference
		this.container.removeInventoryItem(this);
		GUI.unassign(this.container.getOwner(), this);
		super.material(Material.AIR);
		this.refresh();
	}

	@Override
	public String toString() {
		return super.toString() + " (slot=" + this.slot + ", absSlot=" + this.absSlot + ", pg=" + this.page + ")";
	}
}
