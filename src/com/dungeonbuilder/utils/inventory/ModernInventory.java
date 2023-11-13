package com.dungeonbuilder.utils.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.dungeonbuilder.utils.inventory.pane.LoadAsYouGoPane;
import com.dungeonbuilder.utils.inventory.pane.LoadAsYouGoPane.LoadAsYouGoResponse;
import com.dungeonbuilder.utils.inventory.pane.PageablePane;
import com.dungeonbuilder.utils.inventory.pane.PrepopulatedPane;
import com.dungeonbuilder.utils.item.InventoryItem;
import com.dungeonbuilder.utils.item.ItemBuilderParamd.LiveDataChangeEvent;
import com.dungeonbuilder.utils.java.Point2D;

/**
 * Static inventories are so 2017... but Bukkit/Spigot/PaperMC still don't have
 * any inventory utilities!<br/>
 * So here it is, a custom built, sleek, and most importantly - <b>modern</b>
 * inventory utility.
 */
public class ModernInventory {

	@FunctionalInterface
	public interface TitleGenerator {
		/**
		 * @param page - range of [1, infinity).
		 * @return generated title for page.
		 */
		String genTitle(int page);
	}

	public static final TitleGenerator TITLE(String s) {
		return page -> s;
	}

	@FunctionalInterface
	public interface BorderGenerator {
		InventoryItem genBorderItem(int idx);
	}

	public static final BorderGenerator BORDER(InventoryItem is) {
		// Clone the InventoryItem so it doesn't return the same reference at each slot.
		return idx -> idx == 0 ? is : new InventoryItem(is);
	}

	public static final BorderGenerator BORDER(InventoryItem is1, InventoryItem is2) {
		// Alternates between is1 and is2
		// Clone the InventoryItem so it doesn't return the same reference at each slot.
		return idx -> idx % 2 == 0 ? new InventoryItem(is1) : new InventoryItem(is2);
	}

	public static final ModernInventory create(UUID ownerId, TitleGenerator titleGen) {
		return new ModernInventory(ownerId, titleGen);
	}

	public static final int FULL_CHEST_INV_SIZE = 9 * 6,
			FULL_CHEST_INV_SIZE_SUBTRACT_BORDERS = FULL_CHEST_INV_SIZE - 2 * 9 // top and bottom rows
					- 2 * 4 // left and right columns
	;

	private boolean bordered;

	private HashSet<InventoryItem> items;
	// private HashMap<Integer, HashSet<InventoryItem>> itemsPerPage;

	private UUID ownerId;

	private TitleGenerator titleGen;

	private HashMap<String, Object> liveData = new HashMap<String, Object>();

	public Set<PageablePane> panes = new HashSet<>();

	// /**
	// * Indexed from 1 to match {@link #openPage(int)}.
	// */
	// private int currentPageIdx = 1;

	public ModernInventory(UUID ownerId, TitleGenerator titleGen) {
		this.bordered = false;
		this.items = new HashSet<>();
		this.ownerId = ownerId;
		this.titleGen = titleGen;
	}

	public ModernInventory border(BorderGenerator borderGen) {
		if (!this.panes.isEmpty() || !this.items.isEmpty()) {
			// This is not allowed because the position of the items depend on whether the
			// inventory is bordered or not.
			// If the Inventory is not bordered when the items are added they may be
			// overriden by a border that is placed afterwards.
			throw new UnsupportedOperationException(
					"Called border on an Inventory after items have already been added!");
		}
		if (this.bordered) {
			throw new UnsupportedOperationException("Called border on an Inventory that is already bordered!");
		}
		this.bordered = true;
		for (int y : new int[] { 0, 5 }) {
			for (int x = 0; x < 9; x++) {
				int itemIdx = x + y * 9;
				InventoryItem borderItem = borderGen.genBorderItem(itemIdx).container(this).slot(itemIdx)
						.absSlot(itemIdx);
				this.items.add(borderItem);
			}
		}
		for (int x : new int[] { 0, 8 }) {
			for (int y = 1; y < 5; y++) {
				int itemIdx = x + y * 9;
				InventoryItem borderItem = borderGen.genBorderItem(itemIdx).container(this).slot(itemIdx)
						.absSlot(itemIdx);
				this.items.add(borderItem);
			}
		}
		return this;
	}

	private ModernInventory pages_(List<List<InventoryItem>> pagesItems) {
		// Automatically create a LoadAsYouGoPane that fills the inventory size.
		Point2D start = this.bordered ? Point2D.at(1, 1) : Point2D.at(0, 0),
				end = this.bordered ? Point2D.at(7, 4) : Point2D.at(8, 5);
		LoadAsYouGoPane loadAsYouGoPane = new LoadAsYouGoPane(start, end, input -> {
			int pageNum = input.pageNum();
			List<InventoryItem> pageItems = pagesItems.get(pageNum);
			pageItems.forEach(pageItem -> pageItem.container(this));
			return new LoadAsYouGoResponse(pageItems, pageNum < pagesItems.size() - 1);
		});
		loadAsYouGoPane.zIndex = -1;
		this.addPane(loadAsYouGoPane);
		return this;
	}

	/**
	 * Note: if there is a border, the inventory slot location indices (that were
	 * assigned prior to running this method) will be normalized to the inner pane.
	 * 
	 * @param pages - Varargs array of pages (each page being a List of
	 *              {@link InventoryItem}) to populate the Inventory.<br/>
	 *              Note that:
	 *              <ul>
	 *              <li>{@link InventoryItem#slot(int)} should be assigned before
	 *              calling this method.
	 *              </ul>
	 * @return
	 */
	@SafeVarargs
	public final ModernInventory pages(List<InventoryItem>... pages) {
		// Wrap in new ArrayList to remove the direct link between the list and items
		// array
		// Otherwise the remove method may not be supported
		return this.pages_(new ArrayList<>(Arrays.asList(pages)));
	}

	/**
	 * 
	 * @param items - List of {@link InventoryItem} to populate the Inventory. Note
	 *              that {@link InventoryItem#slot(int)} will be assigned in this
	 *              method.
	 * @return
	 */
	public ModernInventory items(List<InventoryItem> items) {
		int availableSlots = this.bordered ? FULL_CHEST_INV_SIZE_SUBTRACT_BORDERS : FULL_CHEST_INV_SIZE;
		if (items.size() >= availableSlots) {
			// Automatically create a PrepopulatedPane that fills the inventory size.
			Point2D start = this.bordered ? Point2D.at(1, 1) : Point2D.at(0, 0),
					end = this.bordered ? Point2D.at(7, 4) : Point2D.at(8, 5);
			PrepopulatedPane prepopPane = new PrepopulatedPane(start, end, items);
			prepopPane.zIndex = -1;
			this.addPane(prepopPane);
		} else {
			for (int i = 0; i < items.size(); i++) {
				int absSlot = i;
				if (this.bordered) {
					int contentX = (i % 7);
					int contentY = (i / 7);
					int realX = contentX + 1; // 1 accounts for the border piece on the left of each row
					int realY = contentY + 1; // 1 accounts for the border pieces on the top row
					absSlot = realY * 9 + realX;
				}
				this.items.add(items.get(i).container(this).slot(i).absSlot(absSlot));
			}
		}
		//// Wrap in new ArrayList to remove the direct link between the list and items
		//// array
		//// Otherwise the remove method may not be supported
		// List<InventoryItem> list = new
		//// ArrayList<InventoryItem>(Arrays.asList(items));// new
		//// ArrayList<InventoryItem>();
		// int pageIdx = 1;
		//// Using do... while forces at least one page to exist even if there are no
		//// items.
		// do {
		// HashSet<InventoryItem> setOnPage = new HashSet<InventoryItem>();
		// final int pageIdxF = pageIdx;
		// if (pageIdxF > 1) {
		//// Next linker (from previous page to current page)
		// InventoryItem nextPageLinker = new
		//// InventoryItem().material(Material.SOUL_LANTERN)
		// .name(Component.text("Next (Pg " + pageIdx +
		//// ")").color(NamedTextColor.AQUA)).container(this)
		// .click(e -> {
		// e.player().closeInventory(); // Unassigns current page
		// this.currentPageIdx = pageIdxF;
		// GUI.assign(this.ownerId, this.itemsPerPage.get(this.currentPageIdx));
		// openPage(this.currentPageIdx);
		// }).slot(6 * 9 - 1).absSlot(6 * 9 - 1).page(pageIdxF);
		// HashSet<InventoryItem> newItemsOnLastPage = this.itemsPerPage.get(pageIdx -
		//// 1);
		// newItemsOnLastPage.add(nextPageLinker);
		// this.itemsPerPage.put(pageIdx - 1, newItemsOnLastPage);
		//// Prev linker (from current page to previous page)
		// InventoryItem prevPageLinker = new
		//// InventoryItem().material(Material.SOUL_LANTERN)
		// .name(Component.text("Prev (Pg " + (pageIdx - 1) +
		//// ")").color(NamedTextColor.AQUA))
		// .container(this).click(e -> {
		// e.player().closeInventory(); // Unassigns current page
		// this.currentPageIdx = pageIdxF - 1;
		// GUI.assign(this.ownerId, this.itemsPerPage.get(this.currentPageIdx));
		// openPage(this.currentPageIdx);
		// }).slot(5 * 9).absSlot(5 * 9).page(pageIdxF);
		// setOnPage.add(prevPageLinker);
		// }
		// for (int i = 0; i < sizePerPage; i++) {
		// if (list.isEmpty())
		// break;
		// InventoryItem invItem =
		//// list.remove(0).container(this).slot(i).page(pageIdxF);
		// int slotActualPlacement = i;
		// if (this.bordered) {
		// int contentX = (i % 7);
		// int contentY = (i / 7);
		// int realX = contentX + 1; // 1 accounts for the border piece on the left of
		//// each row
		// int realY = contentY + 1; // 1 accounts for the border pieces on the top row
		// slotActualPlacement = realY * 9 + realX;
		// }
		// invItem.absSlot(slotActualPlacement);
		// setOnPage.add(invItem);
		// }
		// this.itemsPerPage.put(pageIdx, setOnPage);
		// pageIdx++;
		// } while (!list.isEmpty());
		return this;
	}

	public ModernInventory items(InventoryItem... items) {
		return this.items(new ArrayList<>(Arrays.asList(items)));
	}

	@SafeVarargs
	public final ModernInventory withInitialLiveData(Entry<String, Object>... entries) {
		for (Entry<String, Object> entry : entries) {
			this.liveData.put(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public void setLiveData(String key, Object value) {
		this.liveData.put(key, value);
		// Collect consumers to call after looping over the items on the current page
		// since this may change as a result of calling a consumer!
		// Prevents ConcurrentModificationException
		List<Consumer<LiveDataChangeEvent>> consumers = new ArrayList<>();
		List<InventoryItem> invItems = new ArrayList<>();
		Consumer<InventoryItem> liveDataItemCollector = invItem -> {
			Consumer<LiveDataChangeEvent> liveDataChangeConsumer = invItem.liveDataChangeConsumers.get(key);
			if (liveDataChangeConsumer != null) {
				consumers.add(liveDataChangeConsumer);
				invItems.add(invItem);
			}
		};
		this.panes.forEach(pane -> {
			pane.getItems().forEach(liveDataItemCollector);
		});
		this.items.forEach(liveDataItemCollector);
		for (int i = 0; i < consumers.size(); i++) {
			Consumer<LiveDataChangeEvent> liveDataChangeConsumer = consumers.get(i);
			InventoryItem invItem = invItems.get(i);
			liveDataChangeConsumer.accept(new LiveDataChangeEvent(invItem, value));
		}
	}

	public Object getLiveData(String key) {
		return this.liveData.get(key);
	}

	public void updateItem(InventoryItem invItem) {
		Bukkit.getPlayer(this.ownerId).getOpenInventory().getTopInventory().setItem(invItem.absSlot,
				invItem.getInnerIS());
	}

	public void removeInventoryItem(InventoryItem invItem) {
		// Attempt to remove from items and all panes (incase the item is within a
		// pane).
		this.items.remove(invItem);
		this.panes.forEach(pane -> {
			pane.removeInventoryItem(invItem);
		});
	}

	public void addPane(PageablePane pane) {
		pane.captureInventory(this);
		this.panes.add(pane);
	}

	public void open() {
		Player player = Bukkit.getPlayer(this.ownerId);
		String pageTitle = this.titleGen.genTitle(0);
		@SuppressWarnings("deprecation")
		Inventory pageInv = Bukkit.createInventory(Bukkit.getPlayer(this.ownerId), FULL_CHEST_INV_SIZE, pageTitle);
		this.items.forEach(invItem -> {
			pageInv.setItem(invItem.absSlot, invItem.getInnerIS());
		});
		// Since openInventory calls closeInventory, run GUI.assign after openInventory
		// (otherwise closeInventory would remove the assignments).
		player.openInventory(pageInv);
		GUI.assign(this.ownerId, this.items);
		// Sort panes by zIndex before rendering to prevent lower zIndex panes from
		// clearing items that were already rendered by higher zIndex panes.
		this.panes.stream().sorted(new Comparator<PageablePane>() {
			@Override
			public int compare(PageablePane p1, PageablePane p2) {
				return p1.zIndex - p2.zIndex;
			}
		}).forEach(pane -> pane.render(this.ownerId, pageInv));
	}

	public UUID getOwner() {
		return this.ownerId;
	}
}