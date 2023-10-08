package dev.geco.gholo.events;

import org.bukkit.event.*;
import org.bukkit.event.player.*;

import dev.geco.gholo.GHoloMain;

public class PlayerEvents implements Listener {

    private final GHoloMain GPM;

    public PlayerEvents(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @EventHandler
    public void PJoiE(PlayerJoinEvent Event) {

        GPM.getUManager().loginCheckForUpdates(Event.getPlayer());

        GPM.getTManager().runDelayed(() -> {
            GPM.getHoloSpawnManager().clearPlayerCache(Event.getPlayer());
        }, 1);
    }

    @EventHandler
    public void PQuiE(PlayerQuitEvent Event) { GPM.getHoloSpawnManager().clearPlayerCache(Event.getPlayer()); }

    @EventHandler
    public void PChaWE(PlayerChangedWorldEvent Event) {

        GPM.getTManager().runDelayed(() -> {
            GPM.getHoloSpawnManager().clearPlayerCache(Event.getPlayer());
        }, 1);
    }

}