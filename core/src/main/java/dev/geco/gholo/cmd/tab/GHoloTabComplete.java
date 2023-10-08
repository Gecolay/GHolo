package dev.geco.gholo.cmd.tab;

import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class GHoloTabComplete implements TabCompleter {

    private final GHoloMain GPM;

    public GHoloTabComplete(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if(Sender instanceof Player) {

            if(Args.length == 1) {

                if(GPM.getPManager().hasPermission(Sender, "Holo")) {

                    complete.add("help");
                    complete.add("create");
                    complete.add("info");
                    complete.add("remove");
                    complete.add("tp");
                    complete.add("tphere");
                    complete.add("tpto");
                    complete.add("addrow");
                    complete.add("removerow");
                    complete.add("setrow");
                    complete.add("insertrow");
                    complete.add("copy");
                    complete.add("range");
                    complete.add("align");
                    complete.add("image");
                    complete.add("import");
                }
            } else if(Args.length == 2) {

                if(GPM.getPManager().hasPermission(Sender, "Holo")) {

                    if(Args[0].equalsIgnoreCase("info") || Args[0].equalsIgnoreCase("remove") || Args[0].equalsIgnoreCase("tp") || Args[0].equalsIgnoreCase("tphere") || Args[0].equalsIgnoreCase("tpto") || Args[0].equalsIgnoreCase("addrow") || Args[0].equalsIgnoreCase("removerow") || Args[0].equalsIgnoreCase("setrow") || Args[0].equalsIgnoreCase("insertrow") || Args[0].equalsIgnoreCase("copy") || Args[0].equalsIgnoreCase("range") || Args[0].equalsIgnoreCase("align") || Args[0].equalsIgnoreCase("condition") || Args[0].equalsIgnoreCase("image")) for(GHolo holo : GPM.getHoloManager().getHolos()) complete.add(holo.getId());
                    if(Args[0].equalsIgnoreCase("import")) complete.addAll(GPM.getHoloImportManager().PLUGIN_IMPORTS);
                }
            } else if(Args.length == 3) {

                if(GPM.getPManager().hasPermission(Sender, "Holo")) {

                    if(Args[0].equalsIgnoreCase("removerow") || Args[0].equalsIgnoreCase("setrow") || Args[0].equalsIgnoreCase("insertrow")) {

                        GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                        if(holo != null) for(int row = 1; row <= holo.getRows().size(); row++) complete.add(String.valueOf(row));
                    }
                    if(Args[0].equalsIgnoreCase("copy")) for(GHolo holo : GPM.getHoloManager().getHolos()) complete.add(holo.getId());
                    if(Args[0].equalsIgnoreCase("align")) {

                        complete.add("x");
                        complete.add("y");
                        complete.add("z");
                        complete.add("xz");
                    }
                    if(Args[0].equalsIgnoreCase("image")) complete.addAll(ImageUtil.IMAGE_TYPES);
                }
            } else if(Args.length == 4) {

                if(GPM.getPManager().hasPermission(Sender, "Holo")) {

                    if(Args[0].equalsIgnoreCase("align")) for(GHolo holo : GPM.getHoloManager().getHolos()) complete.add(holo.getId());
                    if(Args[0].equalsIgnoreCase("image")) {

                        if(Args[2].equalsIgnoreCase("avatar") || Args[2].equalsIgnoreCase("helm")) for(OfflinePlayer t : Bukkit.getOfflinePlayers()) complete.add(t.getName());
                        if(Args[2].equalsIgnoreCase("file")) complete.addAll(Arrays.asList(Objects.requireNonNull(ImageUtil.IMAGE_FOLDER.list())));
                    }
                }
            }

            if(!Args[Args.length - 1].isEmpty()) {

                for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                complete.clear();
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }

}