package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.configuration.file.*;

import dev.geco.gholo.GHoloMain;

public class HoloImportManager {

    private final GHoloMain GPM;

    public HoloImportManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public static List<String> PLUGIN_IMPORTS = Collections.singletonList(
            "holographic_displays"
    );

    public boolean importFromPlugin(String Plugin) {

        String plugin = Plugin.toLowerCase();

        boolean imported = false;

        try {

            switch (plugin) {
                case "holographic_displays":
                    File contentFile = new File("plugins/HolographicDisplays/database.yml");

                    if(contentFile.exists()) {

                        FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);

                        List<String> lines = new ArrayList<>();
                        try { lines.addAll(fileContent.getConfigurationSection("").getKeys(false)); } catch(Exception ignored) { }

                        for(String line : lines) {

                            if(!GPM.getHoloManager().existsHolo(line)) {

                                String[] a;

                                if(fileContent.contains(line + ".location")) {

                                    a = fileContent.getString(line + ".location", "").split(",");
                                } else {

                                    String basePath = line + ".position.";
                                    a = new String[4];
                                    a[0] = fileContent.getString(basePath + "world");
                                    a[1] = fileContent.getString(basePath + "x");
                                    a[2] = fileContent.getString(basePath + "y");
                                    a[3] = fileContent.getString(basePath + "z");
                                }

                                World world = Bukkit.getWorld(a[0]);

                                if(world != null) {
                                    List<String> finalContent = fileContent.getStringList(line + ".lines");
                                    List<String> removeContent = fileContent.getStringList(line + ".lines");
                                    for(String removeContentLine : removeContent) if(removeContentLine.equalsIgnoreCase("null")) finalContent.remove("null");
                                    GPM.getHoloManager().insertHolo(line, new Location(world, Double.parseDouble(a[1]), Double.parseDouble(a[2]) - 0.51, Double.parseDouble(a[3])), finalContent);
                                    imported = true;
                                }
                            }
                        }
                    }

                    break;
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return imported;
    }

}
