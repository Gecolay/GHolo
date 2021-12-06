package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.file.*;
import org.bukkit.scheduler.BukkitRunnable;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.values.Values;

public class HoloManager {
	
	private final GHoloMain GPM;
	
	public HoloManager(GHoloMain GHoloMain) {
		
		GPM = GHoloMain;

		spawnmanager = NMSManager.isNewerOrVersion(17, 0) ? (IHoloSpawnManager) NMSManager.getPackageObject("gholo", "manager.HoloSpawnManager", GPM) : new HoloSpawnManager(GPM);

	}
	
	private File HData;
	
	private FileConfiguration HD;
	
	private HashMap<String, Holo> holos = new HashMap<String, Holo>();
	
	private BukkitRunnable r, e;
	
	private IHoloSpawnManager spawnmanager;
	
	public IHoloSpawnManager getSpawnManager() { return spawnmanager; }
	
	public Holo getHolo(String Id) { return holos.get(Id.toLowerCase()); }
	
	public boolean existsHolo(String Id) { return getHolo(Id) != null; }
	
	public void insertHolo(String Id, Location L, List<String> C) { insertHolo(Id, L, C, null, -1); }
	
	public void insertHolo(String Id, Location L, List<String> C, String Q) { insertHolo(Id, L, C, Q, -1); }
	
	public void insertHolo(String Id, Location L, List<String> C, String Q, int R) {
		
		Holo h = new Holo(Id.toLowerCase(), L, C, Q, R);
		
		holos.put(Id.toLowerCase(), h);
		
		if(L != null && L.getWorld() != null) getSpawnManager().registerHolo(h);
		
	}
	
	public void removeHolo(String Id) {
		
		getSpawnManager().unregisterHolo(getHolo(Id), true);
		
		holos.remove(Id.toLowerCase());
		
	}
	
	public void moveHolo(String Id, Location L) {
		
		Holo h = getHolo(Id);
		
		h.setLocation(L);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void addHoloContent(String Id, String C) {
		
		Holo h = getHolo(Id);
		
		h.addContent(C);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void removeHoloContent(String Id, int R) {
		
		Holo h = getHolo(Id);
		
		h.removeContent(R);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void setHoloContent(String Id, List<String> C) {
		
		Holo h = getHolo(Id);
		
		h.setContent(C);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void setHoloContent(String Id, int R, String C) {
		
		Holo h = getHolo(Id);
		
		h.setContent(R, C);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void insertHoloContent(String Id, int R, String C) {
		
		Holo h = getHolo(Id);
		
		h.insertContent(R, C);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void setHoloCondition(String Id, String C) {
		
		Holo h = getHolo(Id);
		
		h.setCondition(C);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public void setHoloRange(String Id, int R) {
		
		Holo h = getHolo(Id);
		
		h.setRange(R);
		
		getSpawnManager().registerHolo(h);
		
	}
	
	public HashMap<String, Holo> getHolos() { return holos; }
	
	private void startHolos() {
		
		e = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				getSpawnManager().spawn();
				
			}
			
		};
		
		e.runTaskTimerAsynchronously(GPM, 0, 2);
		
	}
	
	private void stopHolos() {
		
		if(e != null) e.cancel();
		
		getSpawnManager().unregister();
		
	}
	
	public void loadHolos() {
		
		HData = new File("plugins/" + GPM.NAME, Values.DATA_PATH + "/h" + Values.DATA_FILETYP);
		HD = YamlConfiguration.loadConfiguration(HData);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(HD.getConfigurationSection("H") != null) {
					
					for(Entry<String, Object> i : HD.getConfigurationSection("H").getValues(false).entrySet()) {
						
						Location L = GPM.getFormatUtil().getStringLocation(HD.getString("H." + i.getKey() + ".l"));
						List<String> C = HD.getStringList("H." + i.getKey() + ".c");
						String Q = HD.getString("H." + i.getKey() + ".q");
						int R = HD.getInt("H." + i.getKey() + ".r", -1);
						
						if(L != null) insertHolo(i.getKey(), L, C, Q, R);
						
					}
					
				}
				
				startAutoSave();
				startHolos();
				
			}
			
		}.runTaskLaterAsynchronously(GPM, 3);
		
	}
	
	public void quickSaveHolos() {
		
		HD.set("H", null);
		
		for(Holo h : holos.values()) {

			String l = GPM.getFormatUtil().getLocationString(h.getLocation());

			if(l == null) continue;

			HD.set("H." + h.getId() + ".l",  l);
			if(h.getContent().size() > 0) HD.set("H." + h.getId() + ".c", h.getContent());
			if(h.getCondition() != null) HD.set("H." + h.getId() + ".q", h.getCondition());
			if(h.getRange() != -1) HD.set("H." + h.getId() + ".r", h.getRange());
			
		}
		
		saveFile(HData, HD);
		
	}
	
	public void saveHolos() {
		
		quickSaveHolos();
		
		stopHolos();
		
		stopAutoSave();
		
	}
	
	private void startAutoSave() {
		
		stopAutoSave();
		
		r = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				quickSaveHolos();
				
			}
			
		};
		
		long t = 20 * 180;
		
		r.runTaskTimerAsynchronously(GPM, t, t);
		
	}
	
	private void stopAutoSave() { if(r != null) r.cancel(); }
	
	private void saveFile(File F, FileConfiguration FC) { try { FC.save(F); } catch (IOException e) { } }
	
}