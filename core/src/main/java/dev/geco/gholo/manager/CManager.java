package dev.geco.gholo.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.geco.gholo.GHoloMain;

public class CManager {
	
	public boolean CHECK_FOR_UPDATES;
	
	public double SPACE_BETWEEN_LINES;
	
	public boolean USE_ARMOR_STANDS;
	
	public HashMap<String, String> SYMBOLS = new HashMap<String, String>();
	
	
	private final GHoloMain GPM;
	
	public CManager(GHoloMain GHoloMain) {
		GPM = GHoloMain;
		reload();
	}
	
	public void reload() {
		
		CHECK_FOR_UPDATES = GPM.getConfig().getBoolean("Options.check-for-update", true);
		SPACE_BETWEEN_LINES = GPM.getConfig().getDouble("Options.space-between-lines", 0.26);
		USE_ARMOR_STANDS = GPM.getConfig().getBoolean("Options.use-armor-stands", true);
		SYMBOLS.clear();
		List<String> L = new ArrayList<>();
		try { for(String l : GPM.getConfig().getConfigurationSection("Options.Symbols").getKeys(false)) L.add(l); } catch (Exception e) { }
		for(String l : L) SYMBOLS.put(l, "" + GPM.getConfig().getString("Options.Symbols." + l).toCharArray()[0]);
		
	}
	
}