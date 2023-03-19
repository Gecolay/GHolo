package dev.geco.gholo.manager;

import dev.geco.gholo.objects.*;

public interface IHoloSpawnManager {

    void registerHolo(GHolo Holo);

    void unregisterHolo(GHolo Holo);

    void spawn();

}