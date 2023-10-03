package dev.geco.gholo.cmd;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.*;

import dev.geco.gholo.*;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class GHoloCommand implements CommandExecutor {

    private final GHoloMain GPM;

    public GHoloCommand(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {
        if(Sender instanceof Player) {
            if(!GPM.getPManager().hasPermission(Sender, "Holo")) {
                GPM.getMManager().sendMessage(Sender, "Messages.command-permission-error");
                return true;
            }
        }
        if(Args.length == 0) {
            GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-use-error");
        } else {
            switch(Args[0].toLowerCase()) {
                case "help":
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.header");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.help");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.create");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.info");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.remove");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.tp");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.tphere");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.addrow");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.removerow");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.setrow");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.insertrow");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.copy");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.range");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.align");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.condition");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.image");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.import");
                    GPM.getMManager().sendMessage(Sender, "GHoloHelp.footer");
                    break;
                case "create":
                    if(Sender instanceof Player) {
                        Player p = (Player) Sender;
                        if(Args.length > 1) {
                            if(!GPM.getHoloManager().existsHolo(Args[1])) {
                                GPM.getHoloManager().insertHolo(Args[1], p.getLocation(), new ArrayList<>());
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-create", "%Holo%", Args[1].toLowerCase());
                            } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-create-exists-error", "%Holo%", Args[1].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-create-use-error");
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
                    break;
                case "info":
                    if(Args.length > 1) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            GPM.getMManager().sendMessage(Sender, "GHoloInfo.header");
                            int r = 1;
                            for(String i : GPM.getHoloManager().getHolo(Args[1]).getContent()) {
                                GPM.getMManager().sendMessage(Sender, "GHoloInfo.row", "%RowId%", "" + r, "%Row%", i);
                                r++;
                            }
                            GPM.getMManager().sendMessage(Sender, "GHoloInfo.footer");
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-info-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-info-use-error");
                    break;
                case "remove":
                    if(Args.length > 1) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            GPM.getHoloManager().removeHolo(GPM.getHoloManager().getHolo(Args[1]), true);
                            GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-remove", "%Holo%", Args[1].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-remove-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-remove-use-error");
                    break;
                case "tp":
                    if(Sender instanceof Player) {
                        Player p = (Player) Sender;
                        if(Args.length > 1) {
                            if(GPM.getHoloManager().existsHolo(Args[1])) {
                                p.teleport(GPM.getHoloManager().getHolo(Args[1]).getLocation(), TeleportCause.COMMAND);
                            } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tp-exists-error", "%Holo%", Args[1].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tp-use-error");
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
                    break;
                case "tphere":
                    if(Sender instanceof Player) {
                        Player p = (Player) Sender;
                        if(Args.length > 1) {
                            if(GPM.getHoloManager().existsHolo(Args[1])) {
                                GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                holo.setLocation(p.getLocation());
                                GPM.getHoloManager().saveHolo(holo);
                            } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tphere-exists-error", "%Holo%", Args[1].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tphere-use-error");
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
                    break;
                case "tpto":
                    if(Args.length > 4) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            Location r = GPM.getHoloManager().getHolo(Args[1]).getLocation().clone();
                            try {
                                r.setX(Double.parseDouble(Args[2]));
                                r.setY(Double.parseDouble(Args[3]));
                                r.setZ(Double.parseDouble(Args[4]));
                                GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                holo.setLocation(r);
                                GPM.getHoloManager().saveHolo(holo);
                            } catch (NumberFormatException e) {
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tpto-location-error");
                            }
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tpto-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tpto-use-error");
                    break;
                case "addrow":
                    if(Args.length > 2) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            StringBuilder e = new StringBuilder();
                            for (int f = 2; f <= Args.length - 1; f++) e.append(Args[f]).append(" ");
                            e = new StringBuilder(e.substring(0, e.length() - 1));
                            GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                            holo.addContent(e.toString());
                            GPM.getHoloManager().saveHolo(holo);
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-addrow-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-addrow-use-error");
                    break;
                case "removerow":
                    if(Args.length > 2) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            try {
                                int r = Integer.parseInt(Args[2]);
                                if(r > 0 && GPM.getHoloManager().getHolo(Args[1]).getContent().size() >= r) {
                                    GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                    holo.removeContent(r);
                                    GPM.getHoloManager().saveHolo(holo);
                                } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-row-exists-error", "%Row%", Args[2].toLowerCase());
                            } catch (NumberFormatException e) {
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-row-exists-error", "%Row%", Args[2].toLowerCase());
                            }
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-use-error");
                    break;
                case "setrow":
                    if(Args.length > 3) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            try {
                                int r = Integer.parseInt(Args[2]);
                                if(r > 0 && GPM.getHoloManager().getHolo(Args[1]).getContent().size() >= r) {
                                    StringBuilder e = new StringBuilder();
                                    for (int f = 3; f <= Args.length - 1; f++) e.append(Args[f]).append(" ");
                                    e = new StringBuilder(e.substring(0, e.length() - 1));
                                    GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                    holo.setContent(r, e.toString());
                                    GPM.getHoloManager().saveHolo(holo);
                                } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-row-exists-error", "%Row%", Args[2].toLowerCase());
                            } catch (NumberFormatException e) {
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-row-exists-error", "%Row%", Args[2].toLowerCase());
                            }
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-use-error");
                    break;
                case "insertrow":
                    if(Args.length > 3) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            try {
                                int r = Integer.parseInt(Args[2]);
                                if(r > 0 && GPM.getHoloManager().getHolo(Args[1]).getContent().size() >= r) {
                                    StringBuilder e = new StringBuilder();
                                    for (int f = 3; f <= Args.length - 1; f++) e.append(Args[f]).append(" ");
                                    e = new StringBuilder(e.substring(0, e.length() - 1));
                                    GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                    holo.insertContent(r, e.toString());
                                    GPM.getHoloManager().saveHolo(holo);
                                } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-insertrow-row-exists-error", "%Row%", Args[2].toLowerCase());
                            } catch (NumberFormatException e) {
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-insertrow-row-exists-error", "%Row%", Args[2].toLowerCase());
                            }
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-insertrow-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-insertrow-use-error");
                    break;
                case "copy":
                    if(Args.length > 2) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            if(GPM.getHoloManager().existsHolo(Args[2])) {
                                GHolo holoCopyFrom = GPM.getHoloManager().getHolo(Args[1]);
                                GHolo holoCopyTo = GPM.getHoloManager().getHolo(Args[2]);
                                holoCopyTo.setContent(holoCopyFrom.getContent());
                                GPM.getHoloManager().saveHolo(holoCopyTo);
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-copy", "%Holo%", Args[1].toLowerCase());
                            } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-copy-exists-error", "%Holo%", Args[2].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-copy-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-copy-use-error");
                    break;
                case "range":
                    if(Args.length > 2) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            try {
                                int r = Integer.parseInt(Args[2]);
                                if(r >= -1 && r <= 64) {
                                    GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                    holo.setRange(r);
                                    GPM.getHoloManager().saveHolo(holo);
                                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-range", "%Holo%", Args[1].toLowerCase(), "%Range%", Args[2].toLowerCase());
                                } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-range-range-error", "%Range%", Args[2].toLowerCase());
                            } catch (NumberFormatException e) {
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-range-range-error", "%Range%", Args[2].toLowerCase());
                            }
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-range-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-range-use-error");
                    break;
                case "align":
                    if(Args.length > 3) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            if(Args[2].equalsIgnoreCase("x") || Args[2].equalsIgnoreCase("y") || Args[2].equalsIgnoreCase("z") || Args[2].equalsIgnoreCase("xz")) {
                                if(GPM.getHoloManager().existsHolo(Args[3])) {
                                    Location t = GPM.getHoloManager().getHolo(Args[1]).getLocation().clone();
                                    Location t1 = GPM.getHoloManager().getHolo(Args[3]).getLocation();
                                    if(Args[2].equalsIgnoreCase("x")) t.setX(t1.getX());
                                    if(Args[2].equalsIgnoreCase("y")) t.setY(t1.getY());
                                    if(Args[2].equalsIgnoreCase("z")) t.setZ(t1.getZ());
                                    if(Args[2].equalsIgnoreCase("xz")) {
                                        t.setX(t1.getX());
                                        t.setZ(t1.getZ());
                                    }
                                    GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                                    holo.setLocation(t);
                                    GPM.getHoloManager().saveHolo(holo);
                                } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-align-exists-error", "%Holo%", Args[3].toLowerCase());
                            } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-align-axis-error", "%Axis%", Args[2].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-align-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-align-use-error");
                    break;
                case "image":
                    if(Args.length > 3) {
                        if(GPM.getHoloManager().existsHolo(Args[1])) {
                            GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                            switch(Args[2].toLowerCase()) {
                                case "avatar":
                                    OfflinePlayer t = Bukkit.getOfflinePlayer(Args[3]);
                                    BufferedImage BIA = ImageUtil.getBufferedImage(t, false);
                                    if(BIA != null) {
                                        if(Args.length > 4) {
                                            if(Args[4].contains(":")) {
                                                String[] S = Args[4].split(":");
                                                holo.setContent(new ImageUtil(BIA, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
                                            } else holo.setContent(new ImageUtil(BIA, Integer.parseInt(Args[4])).getLines());
                                        } else holo.setContent(new ImageUtil(BIA).getLines());
                                        GPM.getHoloManager().saveHolo(holo);
                                        GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-avatar", "%Holo%", Args[1].toLowerCase(), "%Avatar%", Args[3]);
                                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-avatar-error", "%Avatar%", Args[3]);
                                    break;
                                case "helm":
                                    OfflinePlayer t1 = Bukkit.getOfflinePlayer(Args[3]);
                                    BufferedImage BIH = ImageUtil.getBufferedImage(t1, true);
                                    if(BIH != null) {
                                        if(Args.length > 4) {
                                            if(Args[4].contains(":")) {
                                                String[] S = Args[4].split(":");
                                                holo.setContent(new ImageUtil(BIH, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
                                            } else holo.setContent(new ImageUtil(BIH, Integer.parseInt(Args[4])).getLines());
                                        } else holo.setContent(new ImageUtil(BIH).getLines());
                                        GPM.getHoloManager().saveHolo(holo);
                                        GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-helm", "%Holo%", Args[1].toLowerCase(), "%Helm%", Args[3]);
                                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-helm-error", "%Helm%", Args[3]);
                                    break;
                                case "file":
                                    File F = new File("plugins/" + GPM.NAME + "/images/" + Args[3]);
                                    if(F.exists()) {
                                        BufferedImage BIF = ImageUtil.getBufferedImage(F);
                                        if(BIF != null) {
                                            if(Args.length > 4) {
                                                if(Args[4].contains(":")) {
                                                    String[] S = Args[4].split(":");
                                                    holo.setContent(new ImageUtil(BIF, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
                                                } else holo.setContent(new ImageUtil(BIF, Integer.parseInt(Args[4])).getLines());
                                            } else holo.setContent(new ImageUtil(BIF).getLines());
                                            GPM.getHoloManager().saveHolo(holo);
                                            GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-file", "%Holo%", Args[1].toLowerCase(), "%File%", Args[3]);
                                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-file-error", "%File%", Args[3]);
                                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-file-exists-error", "%File%", Args[3]);
                                    break;
                                case "url":
                                    BufferedImage BIU = ImageUtil.getBufferedImage(Args[3]);
                                    if(BIU != null) {
                                        if(Args.length > 4) {
                                            if(Args[4].contains(":")) {
                                                String[] S = Args[4].split(":");
                                                holo.setContent(new ImageUtil(BIU, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
                                            } else holo.setContent(new ImageUtil(BIU, Integer.parseInt(Args[4])).getLines());
                                        } else holo.setContent(new ImageUtil(BIU).getLines());
                                        GPM.getHoloManager().saveHolo(holo);
                                        GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-url", "%Holo%", Args[1].toLowerCase(), "%URL%", Args[3]);
                                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-url-error", "%URL%", Args[3]);
                                    break;
                                default:
                                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-use-error");
                                    break;
                            }
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-exists-error", "%Holo%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-image-use-error");
                    break;
                case "import":
                    if(Args.length > 1) {
                        if(HoloImportManager.PLUGIN_IMPORTS.contains(Args[1].toLowerCase())) {
                            boolean imported = GPM.getHoloImportManager().importFromPlugin(Args[1]);
                            if(imported) {
                                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-import-plugin", "%Plugin%", Args[1].toLowerCase());
                            } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-import-plugin-import-error", "%Plugin%", Args[1].toLowerCase());
                        } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-import-plugin-use-error", "%Plugin%", Args[1].toLowerCase());
                    } else GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-import-use-error");
                    break;
                default:
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-use-error");
                    break;
            }
        }
        return true;
    }

}