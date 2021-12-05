package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.Location;

public class Holo {
	
	private String i;
	
	private Location l;
	
	private Location m;
	
	private List<String> c = new ArrayList<>();
	
	private List<UUID> u = new ArrayList<>();
	
	private String q = null;
	
	private int r = -1;
	
	public Holo(String Id, Location L, List<String> C) {
		i = Id;
		l = L;
		c = C;
	}
	
	public Holo(String Id, Location L, List<String> C, String Q) {
		i = Id;
		l = L;
		c = C;
		q = Q;
	}
	
	public Holo(String Id, Location L, List<String> C, String Q, int R) {
		i = Id;
		l = L;
		c = C;
		q = Q;
		r = R;
	}
	
	public String getId() { return i; }
	
	public void setId(String Id) { i = Id; }
	
	public Location getLocation() { return l; }
	
	public void setLocation(Location L) { l = L; }
	
	public Location getMidLocation() { return m; }
	
	public void setMidLocation(Location M) { m = M; }
	
	public List<String> getContent() { return c; }
	
	public void addContent(String C) { c.add(C); }
	
	public void removeContent(int R) { c.remove(R - 1); }
	
	public void setContent(List<String> C) { c = C; }
	
	public void setContent(int R, String C) { c.remove(R - 1); c.add(R - 1, C); }
	
	public void insertContent(int R, String C) { c.add(R - 1, C); }
	
	public List<UUID> getUUIDs() { return u; }
	
	public void addUUID(UUID U) { u.add(U); }
	
	public void removeUUID(UUID U) { u.remove(U); }
	
	public void clearUUIDs() { u.clear(); }
	
	public String getCondition() { return q; }
	
	public void setCondition(String Q) { q = Q; }
	
	public int getRange() { return r; }
	
	public void setRange(int R) { r = R < 0 ? -1 : R > 64 ? 64 : R; }
	
}