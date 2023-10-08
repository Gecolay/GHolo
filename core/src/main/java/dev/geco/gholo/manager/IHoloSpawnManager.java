package dev.geco.gholo.manager;

import org.bukkit.entity.*;

import dev.geco.gholo.objects.*;

public interface IHoloSpawnManager {

    void registerHolo(GHolo Holo);

    void updateHolo(GHolo Holo);

    void unregisterHolo(GHolo Holo);

    void clearPlayerCache(Player Player);

    void spawn();

}