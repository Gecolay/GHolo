package dev.geco.gholo.cmd.tab;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.HoloImportManager;
import dev.geco.gholo.objects.GHolo;
import dev.geco.gholo.util.ImageUtil;

public class GHoloTabComplete implements TabCompleter {
    
    private GHoloMain GPM;
    
    public GHoloTabComplete(GHoloMain GPluginMain) { GPM = GPluginMain; }
    
    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        List<String> ta = new ArrayList<>(), ts = new ArrayList<>();
        if(a.length == 1) {
            if(s.hasPermission(GPM.NAME + ".Holo") || s.hasPermission(GPM.NAME + ".*")) {
                ta.add("help");
                ta.add("create");
                ta.add("info");
                ta.add("remove");
                ta.add("tp");
                ta.add("tphere");
                ta.add("tpto");
                ta.add("addrow");
                ta.add("removerow");
                ta.add("setrow");
                ta.add("insertrow");
                ta.add("copy");
                ta.add("range");
                ta.add("align");
                ta.add("image");
                ta.add("import");
            }
            if(!a[a.length - 1].isEmpty()) {
                for(String r : ta) if(r.toLowerCase().startsWith(a[a.length - 1].toLowerCase())) ts.add(r);
                ta.clear();
            }
        } else if(a.length == 2) {
            if(s.hasPermission(GPM.NAME + ".Holo") || s.hasPermission(GPM.NAME + ".*")) {
                if(a[0].equalsIgnoreCase("info") || a[0].equalsIgnoreCase("remove") || a[0].equalsIgnoreCase("tp") || a[0].equalsIgnoreCase("tphere") || a[0].equalsIgnoreCase("tpto") || a[0].equalsIgnoreCase("addrow") || a[0].equalsIgnoreCase("removerow") || a[0].equalsIgnoreCase("setrow") || a[0].equalsIgnoreCase("insertrow") || a[0].equalsIgnoreCase("copy") || a[0].equalsIgnoreCase("range") || a[0].equalsIgnoreCase("align") || a[0].equalsIgnoreCase("condition") || a[0].equalsIgnoreCase("image")) for(GHolo h : GPM.getHoloManager().getHolos()) ta.add(h.getId());
                if(a[0].equalsIgnoreCase("import")) ta.addAll(HoloImportManager.PLUGIN_IMPORTS);
            }
            if(!a[a.length - 1].isEmpty()) {
                for(String r : ta) if(r.toLowerCase().startsWith(a[a.length - 1].toLowerCase())) ts.add(r);
                ta.clear();
            }
        } else if(a.length == 3) {
            if(s.hasPermission(GPM.NAME + ".Holo") || s.hasPermission(GPM.NAME + ".*")) {
                if(a[0].equalsIgnoreCase("removerow") || a[0].equalsIgnoreCase("setrow") || a[0].equalsIgnoreCase("insertrow")) {
                    GHolo h = GPM.getHoloManager().getHolo(a[1]);
                    if(h != null) for(int i = 1; i <= h.getContent().size(); i++) ta.add("" + i);
                }
                if(a[0].equalsIgnoreCase("copy")) for(GHolo h : GPM.getHoloManager().getHolos()) ta.add(h.getId());
                if(a[0].equalsIgnoreCase("align")) {
                    ta.add("x");
                    ta.add("y");
                    ta.add("z");
                    ta.add("xz");
                }
                if(a[0].equalsIgnoreCase("image")) ta.addAll(ImageUtil.IMAGE_TYPES);
            }
            if(!a[a.length - 1].isEmpty()) {
                for(String r : ta) if(r.toLowerCase().startsWith(a[a.length - 1].toLowerCase())) ts.add(r);
                ta.clear();
            }
        } else if(a.length == 4) {
            if(s.hasPermission(GPM.NAME + ".Holo") || s.hasPermission(GPM.NAME + ".*")) {
                if(a[0].equalsIgnoreCase("align")) for(GHolo h : GPM.getHoloManager().getHolos()) ta.add(h.getId());
                if(a[0].equalsIgnoreCase("image")) {
                    if(a[2].equalsIgnoreCase("avatar") || a[2].equalsIgnoreCase("helm")) for(OfflinePlayer t : Bukkit.getOfflinePlayers()) ta.add(t.getName());
                    if(a[2].equalsIgnoreCase("file")) for(String t : ImageUtil.IMAGE_FOLDER.list()) ta.add(t);
                }
            }
            if(!a[a.length - 1].isEmpty()) {
                for(String r : ta) if(r.toLowerCase().startsWith(a[a.length - 1].toLowerCase())) ts.add(r);
                ta.clear();
            }
        }
        return ta.size() == 0 ? ts : ta;
    }
    
}