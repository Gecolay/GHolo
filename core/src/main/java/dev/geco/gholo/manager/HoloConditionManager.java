package dev.geco.gholo.manager;

import java.util.regex.*;

import org.bukkit.entity.Player;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.values.Values;

public class HoloConditionManager {
	
	private final GHoloMain GPM;
	
	public HoloConditionManager(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	public boolean validateCondition(String C) {
		
		String[] l = C.replace(" ", "").split(",");
		
		if(l.length == 0) return false;
		
		for(String t : l) {
			
			String[] p = t.split(":", 2);
			
			if(p.length != 2 || !Values.CONDITION_TYPES.contains(p[0].toLowerCase())) return false;
			
			if(p[0].equalsIgnoreCase("placeholder") && p[1].split("([\\<\\>\\=]{1,2})").length != 2) return false;
			
		}
		
		return true;
		
	}
	
	public boolean checkCondition(String C, Player P) {
		
		String[] l = C.replace(" ", "").split(",");
		
		boolean r = true;
		
		for(String t : l) {
			
			String[] p = t.split(":", 2);
			
			switch(p[0].toLowerCase()) {
			case "permission":
				r = P.hasPermission(p[1]);
				break;
			case "placeholder":
				String[] z = GPM.getFormatUtil().formatPlaceholders(p[1], P).split("([\\<\\>\\=]{1,2})");
				Matcher m = Pattern.compile("([\\<\\>\\=]{1,2})").matcher(p[1]);
				while(m.find()) {
					switch(m.group()) {
					case "=":
						r = z[0].equalsIgnoreCase(z[1]);
						break;
					case "==":
						r = z[0].equalsIgnoreCase(z[1]);
						break;
					case ">=":
						r = isNumber(z[0]) && isNumber(z[1]) && getNumber(z[0]) >= getNumber(z[1]);
						break;
					case "=>":
						r = isNumber(z[0]) && isNumber(z[1]) && getNumber(z[0]) >= getNumber(z[1]);
						break;
					case ">":
						r = isNumber(z[0]) && isNumber(z[1]) && getNumber(z[0]) > getNumber(z[1]);
						break;
					case "<":
						r = isNumber(z[0]) && isNumber(z[1]) && getNumber(z[0]) < getNumber(z[1]);
						break;
					case "<=":
						r = isNumber(z[0]) && isNumber(z[1]) && getNumber(z[0]) <= getNumber(z[1]);
						break;
					case "=<":
						r = isNumber(z[0]) && isNumber(z[1]) && getNumber(z[0]) <= getNumber(z[1]);
						break;
					case "<>":
						r = !z[0].equalsIgnoreCase(z[1]);
						break;
					case "><":
						r = !z[0].equalsIgnoreCase(z[1]);
						break;
					}
				}
				break;
			}
			
			if(!r) return false;
			
		}
		
		return r;
		
	}
	
	private boolean isNumber(String S) {
		try {
			Double.parseDouble(S);
			return true;
		} catch(NumberFormatException e) { return false; }
	}
	
	private double getNumber(String S) { return Double.parseDouble(S); }
	
}