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

            switch(plugin) {
                case "holographic_displays":

                    File contentFile = new File("plugins/HolographicDisplays/database.yml");

                    if(!contentFile.exists()) return false;

                    FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);

                    for(String line : Objects.requireNonNull(fileContent.getConfigurationSection("")).getKeys(false)) {

                        if(GPM.getHoloManager().existsHolo(line)) continue;

                        String[] args;

                        if(fileContent.contains(line + ".location")) {

                            args = fileContent.getString(line + ".location", "").split(",");
                        } else {

                            String basePath = line + ".position.";
                            args = new String[4];
                            args[0] = fileContent.getString(basePath + "world");
                            args[1] = fileContent.getString(basePath + "x");
                            args[2] = fileContent.getString(basePath + "y");
                            args[3] = fileContent.getString(basePath + "z");
                        }

                        World world = Bukkit.getWorld(args[0]);

                        if(world == null) continue;

                        List<String> finalContent = fileContent.getStringList(line + ".lines");
                        List<String> removeContent = fileContent.getStringList(line + ".lines");
                        for(String removeContentLine : removeContent) if(removeContentLine.equalsIgnoreCase("null")) finalContent.remove("null");

                        GPM.getHoloManager().insertHolo(line, new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]) - 0.51, Double.parseDouble(args[3])), finalContent);

                        imported = true;
                    }

                    break;
                default:
                    return false;
            }
        } catch (Throwable e) {

            e.printStackTrace();
            return false;
        }

        return imported;
    }

}
