package dev.geco.gholo.cmd;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import dev.geco.gholo.GHoloMain;

public class GHoloReloadCommand implements CommandExecutor {
	
	private final GHoloMain GPM;
	
	public GHoloReloadCommand(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if(s instanceof Player) {
			Player p = (Player) s;
			if(p.hasPermission(GPM.NAME + "." + GPM.NAME + "Reload") || p.hasPermission(GPM.NAME + ".*")) {
				GPM.reload(s);
				GPM.getMManager().sendMessage(p, "Messages.command-reload");
			} else GPM.getMManager().sendMessage(p, "Messages.command-permission-error");
		} else {
			GPM.reload(s);
			GPM.getMManager().sendMessage(s, "Messages.command-reload");
		}
		return true;
	}
	
}