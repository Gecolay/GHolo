package dev.geco.gholo.events;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import dev.geco.gholo.GHoloMain;

public class PlayerEvents implements Listener {
	
	private final GHoloMain GPM;
	
	public PlayerEvents(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	@EventHandler
	public void PJoiE(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		if(GPM.getCManager().CHECK_FOR_UPDATES && !GPM.getUManager().isLatestVersion()) {
			String me = GPM.getMManager().getMessage("Plugin.plugin-update", "%Name%", GPM.NAME, "%NewVersion%", GPM.getUManager().getLatestVersion(), "%Version%", GPM.getUManager().getPluginVersion(), "%Path%", GPM.getDescription().getWebsite());
			if(p.hasPermission(GPM.NAME + ".Update") || p.hasPermission(GPM.NAME + ".*")) p.sendMessage(me);
		}
		
	}
	
}