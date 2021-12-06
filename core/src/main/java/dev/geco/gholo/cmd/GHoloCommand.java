package dev.geco.gholo.cmd;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.HoloImportManager;
import dev.geco.gholo.util.ImageUtil;
import dev.geco.gholo.values.Values;

@SuppressWarnings("deprecation")
public class GHoloCommand implements CommandExecutor {
	
	private final GHoloMain GPM;
	
    public GHoloCommand(GHoloMain GHoloMain) { GPM = GHoloMain; }
    
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if(s instanceof Player) {
			Player p = (Player) s;
			if(!(p.hasPermission(GPM.NAME + ".Holo") || p.hasPermission(GPM.NAME + ".*"))) {
				GPM.getMManager().sendMessage(s, "Messages.command-permission-error");
				return true;
			}
		}
		if(a.length == 0) {
			GPM.getMManager().sendMessage(s, "Messages.command-gholo-use-error");
		} else {
			switch(a[0].toLowerCase()) {
			case "help":
				GPM.getMManager().sendMessage(s, "GHoloHelp.header");
				GPM.getMManager().sendMessage(s, "GHoloHelp.help");
				GPM.getMManager().sendMessage(s, "GHoloHelp.create");
				GPM.getMManager().sendMessage(s, "GHoloHelp.info");
				GPM.getMManager().sendMessage(s, "GHoloHelp.remove");
				GPM.getMManager().sendMessage(s, "GHoloHelp.tp");
				GPM.getMManager().sendMessage(s, "GHoloHelp.tphere");
				GPM.getMManager().sendMessage(s, "GHoloHelp.addrow");
				GPM.getMManager().sendMessage(s, "GHoloHelp.removerow");
				GPM.getMManager().sendMessage(s, "GHoloHelp.setrow");
				GPM.getMManager().sendMessage(s, "GHoloHelp.insertrow");
				GPM.getMManager().sendMessage(s, "GHoloHelp.copy");
				GPM.getMManager().sendMessage(s, "GHoloHelp.range");
				GPM.getMManager().sendMessage(s, "GHoloHelp.align");
				GPM.getMManager().sendMessage(s, "GHoloHelp.condition");
				GPM.getMManager().sendMessage(s, "GHoloHelp.image");
				GPM.getMManager().sendMessage(s, "GHoloHelp.import");
				GPM.getMManager().sendMessage(s, "GHoloHelp.footer");
				break;
			case "create":
				if(s instanceof Player) {
					Player p = (Player) s;
					if(a.length > 1) {
						if(!GPM.getHoloManager().existsHolo(a[1])) {
							GPM.getHoloManager().insertHolo(a[1], p.getLocation(), new ArrayList<>());
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-create", "%Holo%", a[1].toLowerCase());
						} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-create-exists-error", "%Holo%", a[1].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-create-use-error");
				} else GPM.getMManager().sendMessage(s, "Messages.command-sender-error");
				break;
			case "info":
				if(a.length > 1) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						GPM.getMManager().sendMessage(s, "GHoloInfo.header");
						int r = 1;
						for(String i : GPM.getHoloManager().getHolo(a[1]).getContent()) {
							GPM.getMManager().sendMessage(s, "GHoloInfo.row", "%RowId%", "" + r, "%Row%", i);
							r++;
						}
						GPM.getMManager().sendMessage(s, "GHoloInfo.footer");
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-info-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-info-use-error");
				break;
			case "remove":
				if(a.length > 1) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						GPM.getHoloManager().removeHolo(a[1]);
						GPM.getMManager().sendMessage(s, "Messages.command-gholo-remove", "%Holo%", a[1].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-remove-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-remove-use-error");
				break;
			case "tp":
				if(s instanceof Player) {
					Player p = (Player) s;
					if(a.length > 1) {
						if(GPM.getHoloManager().existsHolo(a[1])) {
							p.teleport(GPM.getHoloManager().getHolo(a[1]).getLocation(), TeleportCause.COMMAND);
						} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-tp-exists-error", "%Holo%", a[1].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-tp-use-error");
				} else GPM.getMManager().sendMessage(s, "Messages.command-sender-error");
				break;
			case "tphere":
				if(s instanceof Player) {
					Player p = (Player) s;
					if(a.length > 1) {
						if(GPM.getHoloManager().existsHolo(a[1])) {
							GPM.getHoloManager().moveHolo(a[1], p.getLocation());
						} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-tphere-exists-error", "%Holo%", a[1].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-tphere-use-error");
				} else GPM.getMManager().sendMessage(s, "Messages.command-sender-error");
				break;
			case "tpto":
				if(a.length > 4) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						Location r = GPM.getHoloManager().getHolo(a[1]).getLocation().clone();
						try {
							r.setX(Double.parseDouble(a[2]));
							r.setY(Double.parseDouble(a[3]));
							r.setZ(Double.parseDouble(a[4]));
							GPM.getHoloManager().moveHolo(a[1], r);
						} catch(NumberFormatException e) {
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-tpto-location-error");
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-tpto-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-tpto-use-error");
				break;
			case "addrow":
				if(a.length > 2) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						String e = "";
						for (int f = 2; f <= a.length - 1; f++) e += a[f] + " ";
						e = e.substring(0, e.length() - 1);
						GPM.getHoloManager().addHoloContent(a[1], e);
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-addrow-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-addrow-use-error");
				break;
			case "removerow":
				if(a.length > 2) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						try {
							int r = Integer.parseInt(a[2]);
							if(r > 0 && GPM.getHoloManager().getHolo(a[1]).getContent().size() >= r) {
								GPM.getHoloManager().removeHoloContent(a[1], r);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-removerow-row-exists-error", "%Row%", a[2].toLowerCase());
						} catch(NumberFormatException e) {
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-removerow-row-exists-error", "%Row%", a[2].toLowerCase());
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-removerow-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-removerow-use-error");
				break;
			case "setrow":
				if(a.length > 3) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						try {
							int r = Integer.parseInt(a[2]);
							if(r > 0 && GPM.getHoloManager().getHolo(a[1]).getContent().size() >= r) {
								String e = "";
								for (int f = 3; f <= a.length - 1; f++) e += a[f] + " ";
								e = e.substring(0, e.length() - 1);
								GPM.getHoloManager().setHoloContent(a[1], r, e);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-setrow-row-exists-error", "%Row%", a[2].toLowerCase());
						} catch(NumberFormatException e) {
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-setrow-row-exists-error", "%Row%", a[2].toLowerCase());
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-setrow-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-setrow-use-error");
				break;
			case "insertrow":
				if(a.length > 3) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						try {
							int r = Integer.parseInt(a[2]);
							if(r > 0 && GPM.getHoloManager().getHolo(a[1]).getContent().size() >= r) {
								String e = "";
								for (int f = 3; f <= a.length - 1; f++) e += a[f] + " ";
								e = e.substring(0, e.length() - 1);
								GPM.getHoloManager().insertHoloContent(a[1], r, e);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-insertrow-row-exists-error", "%Row%", a[2].toLowerCase());
						} catch(NumberFormatException e) {
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-insertrow-row-exists-error", "%Row%", a[2].toLowerCase());
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-insertrow-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-insertrow-use-error");
				break;
			case "copy":
				if(a.length > 2) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						if(GPM.getHoloManager().existsHolo(a[2])) {
							GPM.getHoloManager().setHoloContent(a[2], GPM.getHoloManager().getHolo(a[1]).getContent());
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-copy", "%Holo%", a[1].toLowerCase());
						} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-copy-exists-error", "%Holo%", a[2].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-copy-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-copy-use-error");
				break;
			case "range":
				if(a.length > 2) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						try {
							int r = Integer.parseInt(a[2]);
							if(r >= 0 && r <= 64) {
								GPM.getHoloManager().setHoloRange(a[1], r);
								GPM.getMManager().sendMessage(s, "Messages.command-gholo-range", "%Holo%", a[1].toLowerCase(), "%Range%", a[2].toLowerCase());
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-range-range-error", "%Range%", a[2].toLowerCase());
						} catch(NumberFormatException e) {
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-range-range-error", "%Range%", a[2].toLowerCase());
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-range-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-range-use-error");
				break;
			case "align":
				if(a.length > 3) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						if(a[2].equalsIgnoreCase("x") || a[2].equalsIgnoreCase("y") || a[2].equalsIgnoreCase("z") || a[2].equalsIgnoreCase("xz")) {
							if(GPM.getHoloManager().existsHolo(a[3])) {
								Location t = GPM.getHoloManager().getHolo(a[1]).getLocation().clone();
								Location t1 = GPM.getHoloManager().getHolo(a[3]).getLocation();
								if(a[2].equalsIgnoreCase("x")) t.setX(t1.getX());
								if(a[2].equalsIgnoreCase("y")) t.setY(t1.getY());
								if(a[2].equalsIgnoreCase("z")) t.setZ(t1.getZ());
								if(a[2].equalsIgnoreCase("xz")) {
									t.setX(t1.getX());
									t.setZ(t1.getZ());
								}
								GPM.getHoloManager().moveHolo(a[1], t);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-align-exists-error", "%Holo%", a[3].toLowerCase());
						} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-align-axis-error", "%Axis%", a[2].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-align-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-align-use-error");
				break;
			case "condition":
				if(a.length > 1) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						if(a.length == 2) {
							GPM.getHoloManager().setHoloCondition(a[1], null);
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-condition-removed", "%Holo%", a[1].toLowerCase());
						} else {
							String e = "";
							for (int f = 2; f <= a.length - 1; f++) e += a[f] + " ";
							e = e.substring(0, e.length() - 1);
							if(GPM.getHoloConditionManager().validateCondition(e)) {
								GPM.getHoloManager().setHoloCondition(a[1], e);
								GPM.getMManager().sendMessage(s, "Messages.command-gholo-condition", "%Holo%", a[1].toLowerCase(), "%Condition%", e);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-condition-valid-error", "%Condition%", e);
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-condition-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-condition-use-error");
				break;
			case "image":
				if(a.length > 3) {
					if(GPM.getHoloManager().existsHolo(a[1])) {
						switch(a[2].toLowerCase()) {
						case "avatar":
							OfflinePlayer t = Bukkit.getOfflinePlayer(a[3]);
							BufferedImage BIA = ImageUtil.getBufferedImage(t, false);
							if(BIA != null) {
								if(a.length > 4) {
									if(a[4].contains(":")) {
										String[] S = a[4].split(":");
										GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIA, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
									} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIA, Integer.parseInt(a[4])).getLines());
								} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIA).getLines());
								GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-avatar", "%Holo%", a[1].toLowerCase(), "%Avatar%", a[3]);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-avatar-error", "%Avatar%", a[3]);
							break;
						case "helm":
							OfflinePlayer t1 = Bukkit.getOfflinePlayer(a[3]);
							BufferedImage BIH = ImageUtil.getBufferedImage(t1, true);
							if(BIH != null) {
								if(a.length > 4) {
									if(a[4].contains(":")) {
										String[] S = a[4].split(":");
										GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIH, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
									} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIH, Integer.parseInt(a[4])).getLines());
								} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIH).getLines());
								GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-helm", "%Holo%", a[1].toLowerCase(), "%Helm%", a[3]);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-helm-error", "%Helm%", a[3]);
							break;
						case "file":
							File F = new File("plugins/" + GPM.NAME + "/" + Values.IMAGES_PATH + "/" + a[3]);
							if(F.exists()) {
								BufferedImage BIF = ImageUtil.getBufferedImage(F);
								if(BIF != null) {
									if(a.length > 4) {
										if(a[4].contains(":")) {
											String[] S = a[4].split(":");
											GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIF, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
										} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIF, Integer.parseInt(a[4])).getLines());
									} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIF).getLines());
									GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-file", "%Holo%", a[1].toLowerCase(), "%File%", a[3]);
								} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-file-error", "%File%", a[3]);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-file-exists-error", "%File%", a[3]);
							break;
						case "url":
							BufferedImage BIU = ImageUtil.getBufferedImage(a[3]);
							if(BIU != null) {
								if(a.length > 4) {
									if(a[4].contains(":")) {
										String[] S = a[4].split(":");
										GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIU, Integer.parseInt(S[0]), Integer.parseInt(S[1])).getLines());
									} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIU, Integer.parseInt(a[4])).getLines());
								} else GPM.getHoloManager().setHoloContent(a[1], new ImageUtil(BIU).getLines());
								GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-url", "%Holo%", a[1].toLowerCase(), "%URL%", a[3]);
							} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-url-error", "%URL%", a[3]);
							break;
						default:
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-use-error");
							break;
						}
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-exists-error", "%Holo%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-image-use-error");
				break;
			case "import":
				if(a.length > 1) {
					if(HoloImportManager.PLUGIN_IMPORTS.contains(a[1].toLowerCase())) {
						boolean b = GPM.getHoloImportManager().importFromPlugin(a[1]);
						if(b) {
							GPM.getMManager().sendMessage(s, "Messages.command-gholo-import-plugin", "%Plugin%", a[1].toLowerCase());
						} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-import-plugin-import-error", "%Plugin%", a[1].toLowerCase());
					} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-import-plugin-use-error", "%Plugin%", a[1].toLowerCase());
				} else GPM.getMManager().sendMessage(s, "Messages.command-gholo-import-use-error");
				break;
			default:
				GPM.getMManager().sendMessage(s, "Messages.command-gholo-use-error");
				break;
			}
		}
		return true;
	}
	
}