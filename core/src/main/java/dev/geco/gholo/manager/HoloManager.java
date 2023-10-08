package dev.geco.gholo.manager;

import java.sql.*;
import java.util.*;

import org.bukkit.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class HoloManager {

    private final GHoloMain GPM;

    public HoloManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public static final int MAX_HOLO_RANGE = 64;

    public static final String EMPTY_STRING = "[]";

    public void createTable() {
        GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo (id TEXT, range INTEGER, l_world TEXT, l_x REAL, l_y REAL, l_z REAL);");
        GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo_row (id TEXT, content TEXT, height REAL);");
    }

    private UUID taskId;

    public GHolo getHolo(String Id) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(Id)).findFirst().orElse(null); }

    public boolean existsHolo(String Id) { return getHolo(Id) != null; }

    public void insertHolo(String Id, Location Location, List<GHoloRow> HoloRows) { insertHolo(Id, Location, HoloRows, -1); }

    public void insertHolo(String Id, Location Location, List<GHoloRow> HoloRows, int Range) { saveHolo(new GHolo(Id.toLowerCase(), Location, HoloRows, Range)); }

    public void unloadHolos() {

        if(taskId != null) GPM.getTManager().cancel(taskId);

        for(GHolo holo : getHolos()) removeHolo(holo, false);

        holos.clear();
    }

    private final List<GHolo> holos = new ArrayList<>();

    public List<GHolo> getHolos() { return new ArrayList<>(holos); }

    private void spawnHolo(GHolo Holo) { GPM.getHoloSpawnManager().registerHolo(Holo); }

    public void saveHolo(GHolo Holo) {

        if(holos.contains(Holo)) removeHolo(Holo, true);

        addToDatabase(Holo);
        holos.add(Holo);

        spawnHolo(Holo);
    }

    private void addToDatabase(GHolo Holo) {

        GPM.getDManager().execute("INSERT INTO holo (id, range, l_world, l_x, l_y, l_z) VALUES (?, ?, ?, ?, ?, ?)", Holo.getId(), Holo.getRange(), Holo.getLocation().getWorld().getName(), Holo.getLocation().getX(), Holo.getLocation().getY(), Holo.getLocation().getZ());
        for(GHoloRow holoRow : Holo.getRows()) GPM.getDManager().execute("INSERT INTO holo_row (id, content, height) VALUES (?, ?, ?)", Holo.getId(), holoRow.getContent(), holoRow.getHeight());
    }

    private void removeFromDatabase(GHolo Holo) {

        GPM.getDManager().execute("DELETE FROM holo WHERE id = ?", Holo.getId());
        GPM.getDManager().execute("DELETE FROM holo_row WHERE id = ?", Holo.getId());
    }

    public void updateHolo(GHolo Holo) {
        removeFromDatabase(Holo);
        addToDatabase(Holo);
        GPM.getHoloSpawnManager().updateHolo(Holo);
    }

    public void removeHolo(GHolo Holo, boolean Database) {
        holos.remove(Holo);
        GPM.getHoloSpawnManager().unregisterHolo(Holo);
        if(Database) removeFromDatabase(Holo);
    }

    public void loadHolos() {

        GPM.getTManager().runDelayed(() -> {

            try {

                ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo");

                while(resultSet.next()) {

                    String id = resultSet.getString("id");
                    World world = Bukkit.getWorld(resultSet.getString("l_world"));
                    double x = resultSet.getDouble("l_x");
                    double y = resultSet.getDouble("l_y");
                    double z = resultSet.getDouble("l_z");
                    if(world == null) continue;
                    Location location = new Location(world, x, y, z);
                    List<GHoloRow> holoRows = new ArrayList<>();
                    ResultSet resultSetContent = GPM.getDManager().executeAndGet("SELECT * FROM holo_row WHERE id = ?", id);
                    while(resultSetContent.next()) {
                        String content = resultSetContent.getString("content");
                        double height = resultSetContent.getDouble("height");
                        holoRows.add(new GHoloRow(content, height));
                    }
                    int range = resultSet.getInt("range");
                    GHolo holo = new GHolo(id, location, holoRows, range);
                    holos.add(holo);
                    spawnHolo(holo);
                }
            } catch (Throwable e) { e.printStackTrace(); }

            taskId = GPM.getTManager().runAtFixedRate(() -> {

                GPM.getHoloSpawnManager().spawn();
            }, 0, 2);
        }, 3);
    }

}