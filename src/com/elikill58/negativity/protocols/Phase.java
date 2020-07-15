package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.utils.ItemUtils;
import com.elikill58.negativity.common.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Phase extends Cheat implements Listeners {

	public Phase() {
		super(CheatKeys.PHASE, false, ItemUtils.WHITE_STAINED_GLASS, CheatCategory.MOVEMENT, true);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		Location loc = p.getLocation();
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (y > 0.1 && (!loc.clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)
				|| !LocationUtils.hasOtherThan(loc.clone().sub(0, 1, 0), Materials.AIR)))
			np.isJumpingWithBlock = true;
		if (y < -0.1)
			np.isJumpingWithBlock = false;
		if (!loc.clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)
				|| !loc.clone().sub(0, 2, 0).getBlock().getType().equals(Materials.AIR)
				|| !loc.clone().sub(0, 3, 0).getBlock().getType().equals(Materials.AIR)
				|| !loc.clone().sub(0, 4, 0).getBlock().getType().equals(Materials.AIR))
			return;
		if (y < 0)
			return;
		if (LocationUtils.hasOtherThan(loc.clone(), Materials.AIR) || LocationUtils.hasOtherThan(loc.clone().sub(0, 1, 0), Materials.AIR))
			return;
		if (!np.isJumpingWithBlock) {
			Negativity.alertMod(ReportType.VIOLATION, p, this, UniversalUtils.parseInPorcent((y * 200) + 20),
					"Player on air. No jumping. DistanceBetweenFromAndTo: " + y + " (ping: " + p.getPing()
							+ "). Warn: " + np.getWarn(this));
		}
	}
}