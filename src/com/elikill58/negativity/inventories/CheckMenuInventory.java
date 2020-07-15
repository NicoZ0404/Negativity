package com.elikill58.negativity.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import java.util.Collections;

import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.common.ChatColor;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.common.inventory.AbstractInventory;
import com.elikill58.negativity.common.inventory.Inventory;
import com.elikill58.negativity.common.inventory.InventoryManager;
import com.elikill58.negativity.common.inventory.InventoryType;
import com.elikill58.negativity.common.inventory.NegativityHolder;
import com.elikill58.negativity.common.item.ItemBuilder;
import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.utils.ItemUtils;
import com.elikill58.negativity.common.utils.Utils;
import com.elikill58.negativity.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;

public class CheckMenuInventory extends AbstractInventory {
	
	public CheckMenuInventory() {
		super(NegativityInventory.CHECK_MENU);
	}
	
	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheckMenuHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory(Inv.NAME_CHECK_MENU, 27, new CheckMenuHolder());
		NegativityPlayer np = NegativityPlayer.getCached(cible.getUniqueId());
		NegativityAccount account = np.getAccount();
		Minerate minerate = account.getMinerate();
		actualizeInventory(p, cible);
		
		inv.set(8, Utils.createSkull(cible.getName(), 1, cible.getName(), ChatColor.GOLD + "UUID: " + cible.getUniqueId(), ChatColor.GREEN + "Version: " + cible.getPlayerVersion().getName()));

		inv.set(10, ItemBuilder.Builder(Materials.DIAMOND_PICKAXE).displayName("Minerate").lore(minerate.getInventoryLoreString()).build());
		inv.set(11, ItemBuilder.Builder(Materials.GRASS).displayName(ChatColor.RESET + "Mods").lore(ChatColor.GRAY + "Forge: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))).build());
		inv.set(12, getWoolItem(p, np.getAccount().isMcLeaks()));
		inv.set(13, ItemBuilder.Builder(Materials.SKELETON_SKULL).displayName(Messages.getMessage(p, "fake_entities")).build());
		//inv.setItem(16, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("DIAMOND_SPADE", "LEGACY_DIAMOND_SPADE"), "Kick"));
		//inv.setItem(17, Utils.createItem(Material.ANVIL, "Ban"));

		inv.set(18, ItemBuilder.Builder(Materials.SPIDER_EYE).displayName(Messages.getMessage(p, "inventory.main.see_inv", "%name%", cible.getName())).build());
		inv.set(19, ItemBuilder.Builder(Materials.EYE_OF_ENDER).displayName(Messages.getMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())).build());
		if(!p.getUniqueId().equals(cible.getUniqueId()))
			inv.set(20, ItemBuilder.Builder(Materials.PACKED_ICE).displayName(Messages.getMessage(p, "inventory.main.freezing", "%name%", cible.getName())).build());
		inv.set(21, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())).build());
		inv.set(22, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.main.active_detection", "%name%", cible.getName())).build());
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.get(i) == null)
				inv.set(i, Inv.EMPTY);
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = p.getOpenInventory();
		if(inv == null || !inv.getType().equals(InventoryType.CHEST)) return;
		NegativityPlayer np = NegativityPlayer.getCached(cible.getUniqueId());
		int betterClick = np.getAccount().getMostClicksPerSecond();
		try {
			inv.set(0, getClickItem(Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), np.ACTUAL_CLICK));
			inv.set(1, getClickItem(Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(betterClick)), betterClick));
			inv.set(2, getClickItem(Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), np.LAST_CLICK));

			inv.set(7, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", cible.getPing() + "")).build());
			inv.set(9, ItemBuilder.Builder(Materials.DIAMOND_SWORD).displayName("Fight: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))).build());
			p.updateInventory();
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}
	
	private static ItemStack getClickItem(String name, int clicks) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
			return ItemBuilder.Builder(getMaterialFromClick(clicks)).displayName(name).build();
		} else {
			// we can use all *_STAINED_CLAY because they will be default STAINED_CLAY
			return createItem(ItemUtils.LIME_STAINED_CLAY, name, 1, getByteFromClick(clicks));
		}
	}

	private static Material getMaterialFromClick(int click) {
		if (click > 25)
			return Materials.RED_STAINED_CLAY;
		else if (click < 25 && click > 15)
			return Materials.ORANGE_STAINED_CLAY;
		else
			return Materials.LIME_STAINED_CLAY;
	}

	private static byte getByteFromClick(int click) {
		if (click > 25)
			return 14;
		else if (click < 25 && click > 15)
			return 4;
		else
			return 5;
	}

	@SuppressWarnings("deprecation")
	private static ItemStack getWoolItem(Player player, boolean b) {
		Material type = (b ? Materials.RED_WOOL : Materials.LIME_WOOL);
		ItemBuilder builder = ItemBuilder.Builder(type);
		if(type.getId().equals("WOOL"))
			builder.durability((short) (b ? 14 : 5));
		builder.displayName(Messages.getMessage(player, "inventory.main.mcleaks_indicator." + (b ? "positive" : "negative")));
		builder.lore(Collections.singletonList(Messages.getMessage(player, "inventory.main.mcleaks_indicator.description")));
		return builder.build();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		Player cible = Inv.CHECKING.get(p);
		if (m == Materials.EYE_OF_ENDER) {
			p.teleport(cible.getLocation());
			p.closeInventory();
			Inv.CHECKING.remove(p);
		} else if (m == Materials.SKELETON_SKULL) {
			if(e.getSlot() == 12) {
				p.closeInventory();
				NegativityPlayer.getCached(cible.getUniqueId()).makeAppearEntities();
			}
		} else if(m == Materials.SPIDER_EYE){
			p.openInventory(cible.getInventory());
			Inv.CHECKING.remove(p);
		} else if(m == Materials.TNT) {
			InventoryManager.open(NegativityInventory.ACTIVED_CHEAT, p, cible);
		} else if(m == Materials.PACKED_ICE) {
			p.closeInventory();
			NegativityPlayer np = NegativityPlayer.getCached(cible.getUniqueId());
			np.isFreeze = !np.isFreeze;
			if (np.isFreeze) {
				if (Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active"))
					InventoryManager.open(NegativityInventory.FREEZE, cible);
				Messages.sendMessage(cible, "inventory.main.freeze", "%name%", p.getName());
			} else
				Messages.sendMessage(cible, "inventory.main.unfreeze", "%name%", p.getName());
		} else if(m == Materials.PAPER) {
			InventoryManager.open(NegativityInventory.ALERT, p, cible);
		} else if(m == Materials.GRASS) {
			InventoryManager.open(NegativityInventory.FORGE_MODS, p, cible);
		} else if(m == Materials.DIAMOND_SHOVEL) {
			// TODO soon implement inv kick
		} else if(m == Materials.ANVIL) {
			// TODO soon implement inv ban
		}
	}
}