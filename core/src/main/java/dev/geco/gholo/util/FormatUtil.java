package dev.geco.gholo.util;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class FormatUtil {
	
	private final GHoloMain GPM;
	
	public FormatUtil(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	public String getLocationString(Location L) {

		if(L == null || L.getWorld() == null) return null;

		return L.getWorld().getUID() + ":" + L.getX() + ":" + L.getY() + ":" + L.getZ();

	}
	
	public Location getStringLocation(String S) {

		if(S == null) return null;

		String[] s = S.split(":");
		
		try {

			try {

				UUID u = UUID.fromString(s[0]);

				return new Location(Bukkit.getWorld(u), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]));

			} catch(IllegalArgumentException e) {

				return new Location(Bukkit.getWorld(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]));

			}
			
		} catch(Exception | Error e) { return null; }
		
	}
	
	public String formatPlaceholders(String S, Player P) {
		
		String s = S;
		
		for(HoloAnimation a : GPM.getAnimationManager().getHoloAnimations().values()) s = s.replace("%" + a.getId() + "%", a.getContext().get(a.getRow()));
		
		try { s = (GPM.getValues().getPAPI() ? PlaceholderAPI.setPlaceholders(P, s) : s); } catch(Exception | Error e) { }
		
		return s;
		
	}
	
	public String formatSymbols(String S) {
		
		String s = S;
		
		for(Entry<String, String> i : GPM.getCManager().SYMBOLS.entrySet()) s = s.replace(i.getKey(), i.getValue());
		
		return s;
		
	}
	
}