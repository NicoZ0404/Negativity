package com.elikill58.negativity.common.inventories;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.FreezeHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.universal.Messages;

public class FreezeInventory extends AbstractInventory {

	public FreezeInventory() {
		super(NegativityInventory.FREEZE);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof FreezeHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Inventory.createInventory(Inv.NAME_FREEZE_MENU, 9, new FreezeHolder());
		inv.set(4, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.mod.you_are_freeze")).build());
		p.openInventory(inv);
	}
	
	@Override
	public void closeInventory(Player p, InventoryCloseEvent e) {
		if(NegativityPlayer.getCached(p.getUniqueId()).isFreeze)
			openInventory(p);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		
	}

}