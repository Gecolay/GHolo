package dev.geco.gholo.manager;

import java.sql.*;
import java.util.*;

import org.bukkit.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class HoloManager {

    private final GHoloMain GPM;

    public HoloManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public void createTable() {
        GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holos (id TEXT, last_update INTEGER, range INTEGER, l_world TEXT, l_x REAL, l_y REAL, l_z REAL);");
        GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holos_content (id TEXT, content TEXT);");
    }

    private UUID taskId;

    public GHolo getHolo(String Id) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(Id)).findFirst().orElse(null); }

    public boolean existsHolo(String Id) { return getHolo(Id) != null; }

    public void insertHolo(String Id, Location Location, List<String> Content) { insertHolo(Id, Location, Content, -1); }

    public void insertHolo(String Id, Location Location, List<String> Content, int Range) { saveHolo(new GHolo(Id.toLowerCase(), Location, Content, Range)); }

    public void unloadHolos() {

        if(taskId != null) GPM.getTManager().cancel(taskId);

        for(GHolo holo : getHolos()) removeHolo(holo, false);

        holos.clear();
        holo_updates.clear();
    }

    private final List<GHolo> holos = new ArrayList<>();

    private final HashMap<String, Long> holo_updates = new HashMap<>();

    public List<GHolo> getHolos() { return new ArrayList<>(holos); }

    private void spawnHolo(GHolo Holo) { GPM.getHoloSpawnManager().registerHolo(Holo); }

    public void saveHolo(GHolo Holo) {

        long update = System.currentTimeMillis();
        holo_updates.put(Holo.getId(), update);

        if(holos.contains(Holo)) removeHolo(Holo, true);

        GPM.getDManager().execute("INSERT INTO holos (id, last_update, range, l_world, l_x, l_y, l_z) VALUES (?, ?, ?, ?, ?, ?, ?)", Holo.getId(), update, Holo.getRange(), Holo.getLocation().getWorld().getName(), Holo.getLocation().getX(), Holo.getLocation().getY(), Holo.getLocation().getZ());
        for(String line : Holo.getContent()) GPM.getDManager().execute("INSERT INTO holos_content (id, content) VALUES (?, ?)", Holo.getId(), line);
        holos.add(Holo);

        spawnHolo(Holo);
    }

    public void removeHolo(GHolo Holo, boolean Database) {
        holos.remove(Holo);
        GPM.getHoloSpawnManager().unregisterHolo(Holo);
        if(!Database) return;
        GPM.getDManager().execute("DELETE FROM holos WHERE id = ?", Holo.getId());
        GPM.getDManager().execute("DELETE FROM holos_content WHERE id = ?", Holo.getId());
    }

    public void loadHolos() {

        GPM.getTManager().runDelayed(() -> {

            try {

                ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holos");

                while(resultSet.next()) {

                    String id = resultSet.getString("id");
                    long lastUpdate = resultSet.getLong("last_update");
                    World world = Bukkit.getWorld(resultSet.getString("l_world"));
                    double x = resultSet.getDouble("l_x");
                    double y = resultSet.getDouble("l_y");
                    double z = resultSet.getDouble("l_z");
                    if(world == null) continue;
                    Location location = new Location(world, x, y, z);
                    List<String> content = new ArrayList<>();
                    ResultSet resultSetContent = GPM.getDManager().executeAndGet("SELECT * FROM holos_content WHERE id = ?", id);
                    while(resultSetContent.next()) {
                        String contentRow = resultSetContent.getString("content");
                        contentRow = GPM.getFormatUtil().formatSymbols(contentRow);
                        contentRow = GPM.getMManager().toFormattedMessage(contentRow);
                        content.add(contentRow);
                    }
                    int range = resultSet.getInt("range");
                    GHolo holo = new GHolo(id, location, content, range);
                    if(!holo_updates.containsKey(id)) {
                        holos.add(holo);
                        holo_updates.put(id, lastUpdate);
                    } else if(holo_updates.get(id) >= lastUpdate) continue;
                    spawnHolo(holo);
                }

                List<GHolo> remove = new ArrayList<>();
                for(GHolo holo : getHolos()) if(!holo_updates.containsKey(holo.getId())) remove.add(holo);
                for(GHolo holo : remove) removeHolo(holo, false);

            } catch (Throwable e) { e.printStackTrace(); }

            taskId = GPM.getTManager().runAtFixedRate(() -> {

                GPM.getHoloSpawnManager().spawn();
            }, 0, 2);
        }, 3);
    }

}