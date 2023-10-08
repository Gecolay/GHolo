package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;

import dev.geco.gholo.manager.*;

public class GHolo {

    private final String id;

    private Location location;

    private Location midLocation;

    private List<String> content;

    private List<Player> players = new ArrayList<>();

    private int range;

    public GHolo(String Id, Location Location, List<String> Content) { this(Id, Location, Content, -1); }

    public GHolo(String Id, Location Location, List<String> Content, int Range) {

        id = Id;
        location = Location.clone();
        content = new ArrayList<>(Content);
        setRange(Range);
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

    public List<Player> getPlayers() { return players; }

    public void setPlayers(List<Player> Players) { players = Players; }

    public void addPlayer(Player Player) { players.add(Player); }

    public void removePlayer(Player Player) { players.remove(Player); }

    public void clearPlayers() { players.clear(); }

    public int getRange() { return range; }

    public void setRange(int Range) { range = Range < 0 ? -1 : Math.min(Range, HoloManager.MAX_HOLO_RANGE); }

}