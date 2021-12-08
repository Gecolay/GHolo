package dev.geco.gholo.objects;

public interface IHoloSpawnManager {
    
    void register();
    
    void registerHolo(Holo H);
    
    void unregister();
    
    void unregisterHolo(Holo H, boolean re);
    
    void spawn();
    
}