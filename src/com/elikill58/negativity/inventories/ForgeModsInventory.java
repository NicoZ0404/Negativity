package com.elikill58.negativity.inventories;

import com.elikill58.negativity.common.ChatColor;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.common.inventory.AbstractInventory;
import com.elikill58.negativity.common.inventory.Inventory;
import com.elikill58.negativity.common.inventory.InventoryManager;
import com.elikill58.negativity.common.inventory.NegativityHolder;
import com.elikill58.negativity.common.item.ItemBuilder;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.utils.InventoryUtils;
import com.elikill58.negativity.inventories.holders.ForgeModsHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ForgeModsInventory extends AbstractInventory {

	public ForgeModsInventory() {
		super(NegativityInventory.FORGE_MODS);
	}
	
	public int slot = 0;
	
	@Override
	public void openInventory(Player mod, Object... args) {
		Player p = (Player) args[0];
		slot = 0;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		Inventory inv = Inventory.createInventory(Inv.NAME_FORGE_MOD_MENU, UniversalUtils.getMultipleOf(np.MODS.size() + 1, 9, 1, 54), new ForgeModsHolder());
		if(np.MODS.size() == 0) {
			inv.set(4, ItemBuilder.Builder(Materials.DIAMOND).displayName("No mods").build());
		} else {
			np.MODS.forEach((name, version) -> {
				inv.set(slot++, ItemBuilder.Builder(Materials.GRASS).displayName(name).lore(ChatColor.GRAY + "Version: " + version).build());
			});
		}
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(mod, "inventory.back")).build());
		InventoryUtils.fillInventory(inv, Inv.EMPTY);
		mod.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if(m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.CHECK_MENU, p, Inv.CHECKING.get(p));
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ForgeModsHolder;
	}
}