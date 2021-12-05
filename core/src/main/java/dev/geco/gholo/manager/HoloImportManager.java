package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import dev.geco.gholo.GHoloMain;

public class HoloImportManager {
	
	private final GHoloMain GPM;
	
    public HoloImportManager(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	public static List<String> PLUGIN_IMPORTS = Arrays.asList("holographic_displays");
	
	public boolean importFromPlugin(String Plugin) {
		
		String p = Plugin.toLowerCase();
		
		if(p.equals(PLUGIN_IMPORTS.get(0))) {
			
			boolean b = false;
			
			File f = new File("plugins/HolographicDisplays/database.yml");
			
			if(f.exists()) {
				
				FileConfiguration FD = YamlConfiguration.loadConfiguration(f);
				
				List<String> L = new ArrayList<>();
				try { for(String l : FD.getConfigurationSection("").getKeys(false)) L.add(l); } catch(Exception e) { }
				
				for(String i : L) {
					
					if(!GPM.getHoloManager().existsHolo(i)) {
						
						String[] a = FD.getString(i + ".location").split(",");
						World w = Bukkit.getWorld(a[0]);
						
						if(w != null) {
							List<String> r = FD.getStringList(i + ".lines");
							List<String> r1 = FD.getStringList(i + ".lines");
							for(String t : r1) if(t.equalsIgnoreCase("null")) r.remove("null");
							GPM.getHoloManager().insertHolo(i, new Location(w, Double.parseDouble(a[1]), Double.parseDouble(a[2]) - 0.51, Double.parseDouble(a[3])), r);
							b = true;
						}
						
					}
					
				}
				
			}
			
			if(b) GPM.getHoloManager().quickSaveHolos();
			
			return b;
			
		}
		
		return false;
		
	}
	
}