package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.configuration.file.*;
import org.bukkit.scheduler.BukkitRunnable;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.values.Values;

public class AnimationManager {
	
	private final GHoloMain GPM;
	
	public AnimationManager(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	private File AData;
	
	private FileConfiguration AD;
	
	private HashMap<String, HoloAnimation> animations = new HashMap<String, HoloAnimation>();
	
	private BukkitRunnable e;
	
	public HoloAnimation getHoloAnimation(String Id) { return animations.get(Id.toLowerCase()); }
	
	public boolean existsHoloAnimation(String Id) { return getHoloAnimation(Id) != null; }
	
	public void insertHoloAnimation(String Id, long T, List<String> C) { animations.put(Id.toLowerCase(), new HoloAnimation(Id.toLowerCase(), T, C)); }
	
	public void removeHoloAnimation(String Id) { animations.remove(Id.toLowerCase()); }
	
	public HashMap<String, HoloAnimation> getHoloAnimations() { return animations; }
	
	private void startHoloAnimations() {
		
		e = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				for(HoloAnimation a : animations.values()) {
					
					a.setActiveTicks(a.getActiveTicks() + 1);
					
					if(a.getActiveTicks() >= a.getTicks()) {
						
						a.setRow(a.getRow() + 1 >= a.getContext().size() ? 0 : a.getRow() + 1);
						
						a.setActiveTicks(0);
						
					}
					
				}
				
			}
			
		};
		
		e.runTaskTimerAsynchronously(GPM, 0, 1);
		
	}
	
	public void stopHoloAnimations() { if(e != null) e.cancel(); }
	
	public void loadHoloAnimations() {
		
		animations.clear();
		
		AData = new File("plugins/" + GPM.NAME, "animations" + Values.YML_FILETYP);
		AD = YamlConfiguration.loadConfiguration(AData);
		
		if(AD.getConfigurationSection("Animations") != null) {
			
			for(String i : AD.getConfigurationSection("Animations").getKeys(false)) insertHoloAnimation(i.toLowerCase(), AD.getLong("Animations." + i + ".ticks"), AD.getStringList("Animations." + i + ".content"));
			
		}
		
		startHoloAnimations();
		
	}
	
}