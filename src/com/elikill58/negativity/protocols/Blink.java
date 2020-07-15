package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.negativity.IPlayerPacketsClearEvent;
import com.elikill58.negativity.common.events.player.PlayerDeathEvent;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Blink extends Cheat implements Listeners {
	
	public Blink() {
		super(CheatKeys.BLINK, true, Materials.COAL_BLOCK, CheatCategory.MOVEMENT, true);
	}

	@EventListener
	public void onPlayerDeath(PlayerDeathEvent e){
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).bypassBlink = true;
	}
	
	@EventListener
	public void onPlayerMove(PlayerMoveEvent e){
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).bypassBlink = false;
	}
	
	@EventListener
	public void onPacketClear(IPlayerPacketsClearEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		if (!(!np.bypassBlink && (p.getGameMode().equals(GameMode.ADVENTURE) || p.getGameMode().equals(GameMode.SURVIVAL))))
			return;
		int ping = p.getPing();
		if (ping < 140) {
			int total = np.ALL_PACKETS - np.PACKETS.getOrDefault(PacketType.Client.KEEP_ALIVE, 0);
			if (total == 0) {
				if(UniversalUtils.parseInPorcent(100 - ping) >= getReliabilityAlert()) {
					boolean last = np.IS_LAST_SEC_BLINK == 2;
					np.IS_LAST_SEC_BLINK++;
					long time_last = System.currentTimeMillis() - np.TIME_OTHER_KEEP_ALIVE;
					if (last) {
						Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - ping),
								"No packet. Last other than KeepAlive: " + np.LAST_OTHER_KEEP_ALIVE + " there is: "
										+ time_last + "ms . Ping: " + ping + ". Warn: " + np.getWarn(this));
					}
				}
			} else
				np.IS_LAST_SEC_BLINK = 0;
		} else 
			np.IS_LAST_SEC_BLINK = 0;
		
		if(ping < getMaxAlertPing()){
			int posLook = np.PACKETS.getOrDefault(PacketType.Client.POSITION_LOOK, 0), pos = np.PACKETS.getOrDefault(PacketType.Client.POSITION, 0);
			int allPos = posLook + pos;
			if(allPos > 60) {
				Negativity.alertMod(allPos > 70 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + allPos), "PositionLook packet: " + posLook + " Position Packet: " + pos +  " (=" + allPos + ") Ping: " + ping + " Warn for Timer: " + np.getWarn(this));
			}
		}
	}
}