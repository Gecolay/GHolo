package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.*;

public class GHolo {
    
    private String id;
    
    private Location location;
    
    private Location midLocation;
    
    private List<String> content;
    
    private final List<UUID> uuids = new ArrayList<>();

    private int range;
    
    public GHolo(String Id, Location Location, List<String> Content) { this(Id, Location, Content, -1); }
    
    public GHolo(String Id, Location Location, List<String> Content, int Range) {
        id = Id;
        location = Location.clone();
        content = new ArrayList<>(Content);
        range = Range;
    }
    
    public String getId() { return id; }
    
    public void setId(String Id) { id = Id; }
    
    public Location getLocation() { return location; }
    
    public void setLocation(Location Location) { location = Location; }
    
    public Location getMidLocation() { return midLocation; }
    
    public void setMidLocation(Location MidLocation) { midLocation = MidLocation; }
    
    public List<String> getContent() { return content; }
    
    public void addContent(String Content) { content.add(Content); }
    
    public void removeContent(int Row) { content.remove(Row - 1); }
    
    public void setContent(List<String> C) { content = C; }
    
    public void setContent(int R, String C) { content.remove(R - 1); content.add(R - 1, C); }
    
    public void insertContent(int R, String C) { content.add(R - 1, C); }
    
    public List<UUID> getUUIDs() { return uuids; }
    
    public void addUUID(UUID U) { uuids.add(U); }
    
    public void removeUUID(UUID U) { uuids.remove(U); }
    
    public void clearUUIDs() { uuids.clear(); }
    
    public int getRange() { return range; }
    
    public void setRange(int R) { range = R < 0 ? -1 : R > 64 ? 64 : R; }
    
}