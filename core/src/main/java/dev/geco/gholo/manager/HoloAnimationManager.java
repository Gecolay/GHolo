package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.configuration.file.*;
import org.bukkit.scheduler.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class HoloAnimationManager {

    private final GHoloMain GPM;

    public HoloAnimationManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    private final HashMap<String, GHoloAnimation> animations = new HashMap<>();

    private BukkitRunnable bukkitRunnable;

    public HashMap<String, GHoloAnimation> getAnimationSet() { return animations; }

    public List<String> getAnimations() { return new ArrayList<>(animations.keySet()); }

    public String getAnimationContent(String Id) {

        GHoloAnimation holoAnimation = animations.getOrDefault(Id.toLowerCase(), null);

        return holoAnimation != null ? holoAnimation.getCurrentContent() : "";
    }

    public void loadHoloAnimations() {

        animations.clear();

        File animationsFile = new File(GPM.getDataFolder(), "animations.yml");
        if(!animationsFile.exists()) GPM.saveResource("animations.yml", false);
        FileConfiguration animationsData = YamlConfiguration.loadConfiguration(animationsFile);

        try {

            for(String id : animationsData.getConfigurationSection("Animations").getKeys(false)) {

                animations.put(id.toLowerCase(), new GHoloAnimation(id.toLowerCase(), animationsData.getLong("Animations." + id + ".ticks", 20), animationsData.getStringList("Animations." + id + ".content")));
            }

            startHoloAnimations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startHoloAnimations() {

        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {

                for(GHoloAnimation animation : animations.values()) {

                    animation.setCurrentTick(animation.getCurrentTick() + 1);

                    if(animation.getCurrentTick() >= animation.getTicks()) {

                        animation.setRow(animation.getRow() + 1 >= animation.getContent().size() ? 0 : animation.getRow() + 1);

                        animation.setCurrentTick(0);
                    }
                }
            }
        };

        bukkitRunnable.runTaskTimerAsynchronously(GPM, 0, 1);
    }

    public void stopHoloAnimations() { if(bukkitRunnable != null) bukkitRunnable.cancel(); }

}