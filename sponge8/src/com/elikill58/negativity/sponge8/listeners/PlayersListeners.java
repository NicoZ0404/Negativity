package com.elikill58.negativity.sponge8.listeners;

import java.net.InetAddress;
import java.util.UUID;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.world.server.ServerLocation;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerDeathEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.sponge8.SpongeNegativity;
import com.elikill58.negativity.sponge8.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge8.impl.entity.SpongePlayer;
import com.elikill58.negativity.sponge8.impl.item.SpongeItemStack;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Scheduler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class PlayersListeners {
	
	@Listener
	public void onPreLogin(ServerSideConnectionEvent.Auth e) {
		UUID playerId = e.profile().uniqueId();
		String playerName = e.profile().name().orElse(null);
		Result result = e.isCancelled() ? Result.KICK_BANNED : Result.ALLOWED;
		InetAddress address = e.connection().address().getAddress();
		String kickMessage = PlainComponentSerializer.plain().serialize(e.message());
		LoginEvent event = new LoginEvent(playerId, playerName, result, address, kickMessage);
		EventManager.callEvent(event);
		e.setMessage(Component.text(event.getKickMessage()));
		e.setCancelled(!event.getLoginResult().equals(Result.ALLOWED));
	}
	
	@Listener
	public void onPlayerJoin(ServerSideConnectionEvent.Join e, @First ServerPlayer p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.uniqueId(), () -> new SpongePlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, PlainComponentSerializer.plain().serialize(e.message()));
		EventManager.callEvent(event);
		e.setMessage(Component.text(event.getJoinMessage()));
		
		if (!ProxyCompanionManager.searchedCompanion) {
			ProxyCompanionManager.searchedCompanion = true;
			Scheduler.getInstance().runDelayed(() -> SpongeNegativity.sendProxyPing(p), 20);
		}
	}
	
	@Listener
	public void onLeave(ServerSideConnectionEvent.Disconnect e, @First ServerPlayer p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.uniqueId(), () -> new SpongePlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, PlainComponentSerializer.plain().serialize(e.message()));
		EventManager.callEvent(event);
		e.setMessage(Component.text(event.getQuitMessage()));
		NegativityPlayer.removeFromCache(p.uniqueId());
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First ServerPlayer p) {
		NegativityPlayer np = NegativityPlayer.getCached(p.uniqueId());
		PlayerMoveEvent event = new PlayerMoveEvent(np.getPlayer(),
			new SpongeLocation(p.world(), e.originalPosition()), new SpongeLocation(p.world(), e.destinationPosition()));
		EventManager.callEvent(event);
		if (event.hasToSet()) {
			e.setDestinationPosition(((ServerLocation) event.getTo().getDefault()).position());
		}
		
		BlockType blockTypeBelowPlayer = p.location().sub(0, 1, 0).blockType();
		if (np.isFreeze && !blockTypeBelowPlayer.equals(BlockTypes.AIR.get()))
			e.setCancelled(true);
		
		if (blockTypeBelowPlayer.equals(BlockTypes.SLIME_BLOCK.get())) {
			np.isUsingSlimeBlock = true;
		} else if (np.isUsingSlimeBlock && (p.require(Keys.ON_GROUND) && !blockTypeBelowPlayer.equals(BlockTypes.AIR.get()))) {
			np.isUsingSlimeBlock = false;
		}
	}
	
	//@Listener TODO
	//public void onTeleport(MoveEntityEvent.Teleport e, @First ServerPlayer p) {
	//	EventManager.callEvent(new PlayerTeleportEvent(SpongeEntityManager.getPlayer(p), new SpongeLocation(e.getFromTransform().getLocation()),
	//		new SpongeLocation(e.getToTransform().getLocation())));
	//}
	
	@Listener
	public void onChat(org.spongepowered.api.event.message.PlayerChatEvent e, @First ServerPlayer p) {
		String message = PlainComponentSerializer.plain().serialize(e.message());
		PlayerChatEvent event = new PlayerChatEvent(SpongeEntityManager.getPlayer(p), message, message);
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@Listener
	public void onDeath(DestructEntityEvent.Death e, @First ServerPlayer p) {
		EventManager.callEvent(new PlayerDeathEvent(SpongeEntityManager.getPlayer(p)));
	}
	
	@Listener
	public void onInteract(InteractEvent e, @First ServerPlayer p) {
		PlayerInteractEvent event = new PlayerInteractEvent(SpongeEntityManager.getPlayer(p), Action.LEFT_CLICK_AIR);
		EventManager.callEvent(event);
		if (event.isCancelled()) {
			if (e instanceof Cancellable) {
				((Cancellable) e).setCancelled(true);
			} else {
				Adapter.getAdapter().getLogger().warn("PlayerInteractEvent was cancelled but Sponge event " + e.getClass().getName() + " can't be cancelled");
			}
		}
	}
	
	@Listener
	public void onItemConsume(UseItemStackEvent.Finish e, @First ServerPlayer p) {
		PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(SpongeEntityManager.getPlayer(p), new SpongeItemStack(e.itemStackInUse().createStack()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
}