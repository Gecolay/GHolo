package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.*;

public class GHolo {

    private final String id;

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

    public Location getLocation() { return location; }

    public void setLocation(Location Location) { location = Location; }

    public Location getMidLocation() { return midLocation; }

    public void setMidLocation(Location MidLocation) { midLocation = MidLocation; }

    public List<String> getContent() { return content; }

    public void addContent(String Content) { content.add(Content); }

    public void removeContent(int Row) { content.remove(Row - 1); }

    public void setContent(List<String> Content) { content = Content; }

    public void setContent(int Row, String Content) { content.remove(Row - 1); content.add(Row - 1, Content); }

    public void insertContent(int Row, String Content) { content.add(Row - 1, Content); }

    public List<UUID> getUUIDs() { return uuids; }

    public void addUUID(UUID U) { uuids.add(U); }

    public void removeUUID(UUID U) { uuids.remove(U); }

    public void clearUUIDs() { uuids.clear(); }

    public int getRange() { return range; }

    public void setRange(int R) { range = R < 0 ? -1 : Math.min(R, 64); }

}