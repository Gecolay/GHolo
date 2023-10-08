package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;

import dev.geco.gholo.manager.*;

public class GHolo {

    private final String id;

    private Location location;

    private Location midLocation;

    private List<GHoloRow> holoRows;

    private List<Player> players = new ArrayList<>();

    private int range;

    public GHolo(String Id, Location Location, List<GHoloRow> HoloRows) { this(Id, Location, HoloRows, -1); }

    public GHolo(String Id, Location Location, List<GHoloRow> HoloRows, int Range) {

        id = Id;
        location = Location.clone();
        holoRows = HoloRows;
        setRange(Range);
    }

    public String getId() { return id; }

    public Location getLocation() { return location; }

    public void setLocation(Location Location) { location = Location; }

    public Location getMidLocation() { return midLocation; }

    public void setMidLocation(Location MidLocation) { midLocation = MidLocation; }

    public List<GHoloRow> getRows() { return holoRows; }

    public void addContent(GHoloRow HoloRow) { holoRows.add(HoloRow); }

    public void removeRow(int Row) { holoRows.remove(Row - 1); }

    public void setRows(List<GHoloRow> HoloRows) { holoRows = new ArrayList<>(HoloRows); }

    public void setRow(int Row, GHoloRow HoloRow) { holoRows.remove(Row - 1); holoRows.add(Row - 1, HoloRow); }

    public void insertRow(int Row, GHoloRow HoloRow) { holoRows.add(Row - 1, HoloRow); }

    public List<Player> getPlayers() { return players; }

    public void setPlayers(List<Player> Players) { players = Players; }

    public void addPlayer(Player Player) { players.add(Player); }

    public void removePlayer(Player Player) { players.remove(Player); }

    public void clearPlayers() { players.clear(); }

    public int getRange() { return range; }

    public void setRange(int Range) { range = Range < 0 ? -1 : Math.min(Range, HoloManager.MAX_HOLO_RANGE); }

}