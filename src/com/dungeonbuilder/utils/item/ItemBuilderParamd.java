package com.dungeonbuilder.utils.item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.dungeonbuilder.utils.inventory.ModernInventory;
import com.dungeonbuilder.utils.nbt.CustomNBT;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

public class ItemBuilderParamd<T extends ItemBuilderParamd<T>> {

	private UUID uuid;
	protected ItemStack is;

	/**
	 * Basic Item builder. Call {@link ItemBuilderParamd#material(Material)} or
	 * {@link ItemBuilderParamd#item(ItemStack)} first to set the item before
	 * changing properties.
	 */
	public ItemBuilderParamd() {
		this.uuid = UUID.randomUUID();
	}

	public ItemBuilderParamd(ItemBuilderParamd<T> copy) {
		this();
		this.is = copy.is.clone();
		this.makeItemUnique();
	}

	private void makeItemUnique() {
		ItemMeta im = this.is.getItemMeta();
		PersistentDataContainer persistentDataContainer = im.getPersistentDataContainer();
		persistentDataContainer.set(CustomNBT.INV_ITEM_UUID_NAMESPACE_KEY, PersistentDataType.STRING,
				this.uuid.toString());
		this.is.setItemMeta(im);
	}

	/**
	 * Note: on an {@link InventoryItem} this will leave the item consumers (click,
	 * live-data) as they are. See {@link InventoryItem#cleanse()} to cleanse the
	 * item if needed.
	 * 
	 * @param is
	 * @param clone - whether to clone the ItemStack or maintain a direct reference.
	 */
	public void setInnerIS(ItemStack is, boolean clone) {
		if (clone) {
			this.is = is.clone();
		} else {
			this.is = is;
		}
		this.makeItemUnique();
	}

	/**
	 * Note: on an {@link InventoryItem} this will leave the item consumers (click,
	 * live-data) as they are. See {@link InventoryItem#cleanse()} to cleanse the
	 * item if needed.
	 * 
	 * @param is
	 */
	public void setInnerIS(ItemStack is) {
		this.setInnerIS(is, false);
	}

	@SuppressWarnings("unchecked")
	public T material(Material mat) {
		if (this.is == null) {
			this.is = new ItemStack(mat, 1);
		} else {
			this.is.setType(mat);
		}
		if (this.is != null && mat != Material.AIR) {
			// If we create a new ItemStack or use setType, we will reset the MaterialData
			// for the ItemStack, so we must assign a new UUID.
			this.makeItemUnique();
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T item(ItemStack is) {
		this.is = is;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T name(Component name) {
		ItemMeta im = this.is.getItemMeta();
		if (im == null) {
			return (T) this;
		}
		im.displayName(Component.text("").decoration(TextDecoration.ITALIC, false).append(name));
		this.is.setItemMeta(im);
		return (T) this;
	}

	public T name(String nameStr) {
		return this.name(Component.text(nameStr));
	}

	@SuppressWarnings("unchecked")
	public T noname() {
		this.name(Component.text(""));
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T amount(int amt) {
		is.setAmount(amt);
		return (T) this;
	}

	/**
	 * Sets the lore on the item to the provided lore.
	 * 
	 * @param lore
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T lore(List<Component> lore) {
		ItemMeta im = this.is.getItemMeta();
		if (im == null) {
			return (T) this;
		}
		im.lore(lore);
		this.is.setItemMeta(im);
		return (T) this;
	}

	/**
	 * Sets the lore on the item to the provided lore.
	 * 
	 * @param lore
	 * @return
	 */
	public T lore(Component... lore) {
		return this.lore(Arrays.asList(lore));
	}

	public T lore(String... lore) {
		Component[] loreComponents = new Component[lore.length];
		for (int i = 0; i < lore.length; i++) {
			loreComponents[i] = Component.text(lore[i]);
		}
		return this.lore(loreComponents);
	}

	/**
	 * Appends the lore to the current lore on the item.
	 * 
	 * @param lore
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T alore(List<Component> lore) {
		ItemMeta im = this.is.getItemMeta();
		if (im == null) {
			return (T) this;
		}
		if (im.hasLore()) {
			List<Component> currentLore = im.lore();
			currentLore.addAll(lore);
			im.lore(currentLore);
		} else {
			im.lore(lore);
		}
		this.is.setItemMeta(im);
		return (T) this;
	}

	/**
	 * Appends the lore to the current lore on the item.
	 * 
	 * @param lore
	 * @return
	 */
	public T alore(Component... lore) {
		return this.alore(Arrays.asList(lore));
	}

	@SuppressWarnings("unchecked")
	public T editlore(int position, List<Component> lore) {
		ItemMeta im = this.is.getItemMeta();
		if (im == null) {
			return (T) this;
		}
		if (im.hasLore()) {
			List<Component> currentLore = im.lore();
			currentLore.remove(position);
			currentLore.addAll(position, lore);
			im.lore(currentLore);
		} else {
			im.lore(lore);
		}
		this.is.setItemMeta(im);
		return (T) this;
	}

	public T editlore(int position, Component... lore) {
		return this.editlore(position, Arrays.asList(lore));
	}

	@SuppressWarnings("unchecked")
	public T insertlore(int position, List<Component> lore) {
		ItemMeta im = this.is.getItemMeta();
		if (im == null) {
			return (T) this;
		}
		if (im.hasLore()) {
			List<Component> currentLore = im.lore();
			currentLore.addAll(position, lore);
			im.lore(currentLore);
		} else {
			im.lore(lore);
		}
		this.is.setItemMeta(im);
		return (T) this;
	}

	public T insertlore(int position, Component... lore) {
		return this.insertlore(position, Arrays.asList(lore));
	}

	public T prependlore(Component... lore) {
		return this.insertlore(0, lore);
	}

	@SuppressWarnings("unchecked")
	public T writeBook(String title, String author, String... pages) {
		for (int i = 0; i < pages.length; i++) {
			pages[i] = ChatColor.translateAlternateColorCodes('&', pages[i]);
		}
		BookMeta bm = (BookMeta) this.is.getItemMeta();
		bm.setTitle(title);
		bm.setAuthor(author);
		bm.setPages(pages);
		this.is.setItemMeta(bm);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T writeBook(String title, String author, Component... pages) {
		BookMeta bm = (BookMeta) this.is.getItemMeta();
		bm.setTitle(title);
		bm.setAuthor(author);
		bm.pages(pages);
		this.is.setItemMeta(bm);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T potionColor(Color color) {
		ItemMeta im = this.is.getItemMeta();
		PotionMeta pm = (PotionMeta) im;
		pm.setColor(color);
		this.is.setItemMeta(pm);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T potionHideEffects() {
		ItemMeta im = this.is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		this.is.setItemMeta(im);
		return (T) this;
	}

	public boolean equalsBukkit(ItemStack other) {
		// Only need to check the attached UUID to save time (rather than checking the
		// entire item is equal).
		return other.getItemMeta().getPersistentDataContainer()
				.get(CustomNBT.INV_ITEM_UUID_NAMESPACE_KEY, PersistentDataType.STRING)
				.equals(this.is.getItemMeta().getPersistentDataContainer().get(CustomNBT.INV_ITEM_UUID_NAMESPACE_KEY,
						PersistentDataType.STRING));
	}

	@SuppressWarnings("unchecked")
	public T setPlayerHeadSkin(OfflinePlayer owningPlayer) {
		if (this.is.getType().equals(Material.PLAYER_HEAD)) {
			SkullMeta sm = (SkullMeta) this.is.getItemMeta();
			sm.setOwningPlayer(owningPlayer);
		}
		return (T) this;
	}

	// InventoryItem methods are defined here and overridden in the InventoryItem
	// class so they can be called on an ItemBuilderParamd object without needing a
	// specific cast to InventoryItem.
	// This allows for chaining parent and child methods together like
	// .name().click() without needing to explicitly cast to the child.
	public record InventoryItemClickEvent(InventoryItem self, InventoryClickEvent e) {

		public Player player() {
			return (Player) this.e.getWhoClicked();
		}
	};

	public record LiveDataChangeEvent(InventoryItem self, Object data) {
	};

	@SuppressWarnings("unchecked")
	public T slot(int slot) {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T click(Consumer<InventoryItemClickEvent> clickConsumer) {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T liveDataChange(String key, Consumer<LiveDataChangeEvent> dataChangeConsumer) {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T container(ModernInventory container) {
		return (T) this;
	}

	// End InventoryItem methods.

	/**
	 * Note: hashCode equality <b>ONLY</b> suggests the two
	 * {@link ItemBuilderParamd} objects are equivalent as a container that hold an
	 * ItemStack. This allows {@link ItemBuilderParamd} to be added to a HashSet at
	 * time A, be mutated for any amount of time, and then checked against the
	 * HashSet at time B. It does not ensure the inner ItemStack has remained
	 * consistent. <br/>
	 * The truth is, everything about this object is mutable, and the way I am using
	 * hashCode just doesn't make sense. Instead, hashCode should be computed based
	 * on the current state of the container (ItemStack inside, current slot of the
	 * container, etc [while ignoring non inner item and location representative
	 * fields fields (like {@link InventoryItem#clickOpt})]). <br/>
	 * I'm not a huge fan of how I am using this, but the alternative would be heavy
	 * in O(n) List operations, and would require a large refactoring of the project
	 * at the time of writing this comment.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.uuid);
	}

	@Override
	public String toString() {
		return this.is.toString();
	}

	public ItemStack getInnerIS() {
		return this.is;
	}
}