package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.configuration.file.*;

import dev.geco.gholo.GHoloMain;

public class HoloImportManager {

    private final GHoloMain GPM;

    public HoloImportManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public List<String> PLUGIN_IMPORTS = new ArrayList<>(); {
        PLUGIN_IMPORTS.add("holographic_displays");
        PLUGIN_IMPORTS.add("decent_holograms");
    }

    public boolean importFromPlugin(String Plugin) {

        String plugin = Plugin.toLowerCase();

        try {

            switch(plugin) {
                case "holographic_displays": return importHolographicDisplays();
                case "decent_holograms": return importDecentHolograms();
                default: return false;
            }
        } catch (Throwable e) {

            e.printStackTrace();
            return false;
        }
    }

    private boolean importHolographicDisplays() {

        boolean imported = false;

        File contentFile = new File("plugins/HolographicDisplays/database.yml");

        if(!contentFile.exists()) return imported;

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

        return imported;
    }

    private boolean importDecentHolograms() {

        boolean imported = false;

        File hologramsDir = new File("plugins/DecentHolograms/holograms");

        if(!hologramsDir.exists()) return imported;

        for(File file : Objects.requireNonNull(hologramsDir.listFiles())) {

            String name = file.getName().replace(".yml", "");

            if(GPM.getHoloManager().existsHolo(name)) continue;

            FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);

            if(!fileContent.getBoolean("enabled", false)) continue;

            int range = fileContent.getInt("display-range", 64);

            String[] args = fileContent.getString("location", "").split(":");

            World world = Bukkit.getWorld(args[0]);

            if(world == null) continue;

            Location location = new Location(world, Double.parseDouble(args[1].replace(",", ".")), Double.parseDouble(args[2].replace(",", ".")) - 0.41, Double.parseDouble(args[3].replace(",", ".")));

            List<String> finalContent = new ArrayList<>();

            for(Object section : Objects.requireNonNull(fileContent.getList("pages"))) {

                if(!(section instanceof LinkedHashMap)) continue;

                Object lines = ((LinkedHashMap<?, ?>) section).get("lines");

                if(!(lines instanceof ArrayList)) continue;

                for(Object contentMap : (ArrayList<?>) lines) {

                    if(!(contentMap instanceof LinkedHashMap)) continue;

                    String content = (String) ((LinkedHashMap<?, ?>) contentMap).get("content");

                    finalContent.add(content);
                }
            }

            GPM.getHoloManager().insertHolo(name, location, finalContent, range);

            imported = true;
        }

        return imported;
    }

}
