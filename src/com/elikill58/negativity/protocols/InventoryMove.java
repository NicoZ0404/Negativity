package com.elikill58.negativity.protocols;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;

public class InventoryMove extends Cheat implements Listeners {

	private final InventoryMove instance;

	public InventoryMove() {
		super(CheatKeys.INVENTORY_MOVE, false, Materials.NETHER_STAR, CheatCategory.MOVEMENT, true, "invmove");
		instance = this;
	}

	@EventListener
	public void onClick(InventoryClickEvent e) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(e.getPlayer(), true, "Click");
	}

	@EventListener
	public void onOpen(InventoryOpenEvent e) {
		if (!(e.getPlayer() instanceof Player) || e.getInventory() == null)
			return;
		NegativityPlayer np = NegativityPlayer.getCached(e.getPlayer().getUniqueId());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove((Player) e.getPlayer(), false, "Open");
	}

	private void checkInvMove(Player p, boolean check, String from) {
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.isSprinting() || p.isSneaking()) {
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (p.isSprinting() || p.isSneaking())
						Negativity.alertMod(ReportType.WARNING, p, instance,
								NegativityPlayer.getCached(p.getUniqueId()).getAllWarn(instance) > 5 ? 100 : 95,
									"Detected when " + from + ". Sprint: " + p.isSprinting() + ", Sneak:" + p.isSneaking(), hoverMsg("main", "%name%", from));
				}
			}, 3);
		} else if (check) {
			final Location lastLoc = p.getLocation().clone();
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
				@Override
				public void run() {
					if(!lastLoc.getWorld().equals(p.getLocation().getWorld()))
						return;
					double dis = lastLoc.distance(p.getLocation());
					if (dis > 1 && (lastLoc.getY() - p.getLocation().getY()) < 0.1
							&& p.getOpenInventory() != null) {
						Negativity.alertMod(ReportType.WARNING, p, instance,
								NegativityPlayer.getCached(p.getUniqueId()).getAllWarn(instance) > 5 ? 100 : 95,
									"Detected when " + from + ", Distance: " + dis + " Diff Y: " + (lastLoc.getY() - p.getLocation().getY()), hoverMsg("main", "%name%", from));
					}
				}
			}, 5);
		}
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}