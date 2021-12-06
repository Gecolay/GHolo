package dev.geco.gholo;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import dev.geco.gholo.cmd.*;
import dev.geco.gholo.cmd.tab.*;
import dev.geco.gholo.events.*;
import dev.geco.gholo.link.*;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;
import dev.geco.gholo.values.*;

public class GHoloMain extends JavaPlugin {

	private FileConfiguration messages;

	public FileConfiguration getMessages() { return messages; }

	private CManager cmanager;

	public CManager getCManager() { return cmanager; }

	private String prefix;

	public String getPrefix() { return prefix; }

	private Values values;

	public Values getValues() { return values; }

	private AnimationManager animationmanager;

	public AnimationManager getAnimationManager() { return animationmanager; }

	private HoloManager holomanager;

	public HoloManager getHoloManager() { return holomanager; }

	private HoloConditionManager holoconditionmanager;

	public HoloConditionManager getHoloConditionManager() { return holoconditionmanager; }

	private HoloImportManager holoimportmanager;

	public HoloImportManager getHoloImportManager() { return holoimportmanager; }

	private UManager umanager;

	public UManager getUManager() { return umanager; }

	private MManager mmanager;

	public MManager getMManager() { return mmanager; }

	private FormatUtil formatutil;

	public FormatUtil getFormatUtil() { return formatutil; }

	public final String NAME = "GHolo";

	public final String RESOURCE = "70913";

	private static GHoloMain GPM;

	public static GHoloMain getInstance() { return GPM; }

	private void setupSettings() {
		copyLangFiles();
		messages = YamlConfiguration.loadConfiguration(new File("plugins/" + NAME + "/" + Values.LANG_PATH, getConfig().getString("Lang.lang") + Values.YML_FILETYP));
		prefix = getMessages().getString("Plugin.plugin-prefix");
		if(!ImageUtil.IMAGE_PATH.exists()) ImageUtil.IMAGE_PATH.mkdir();
		if(!new File("plugins/" + NAME + "/animations" + Values.YML_FILETYP).exists()) saveResource("animations" + Values.YML_FILETYP, false);
		getAnimationManager().loadHoloAnimations();
		getHoloManager().loadHolos();
	}

	private void linkBStats() {
		BStatsLink bstats = new BStatsLink(getInstance(), 4921);
		bstats.addCustomChart(new BStatsLink.SimplePie("plugin_language", new Callable<String>() {
			@Override
			public String call() {
				return getConfig().getString("Lang.lang").toLowerCase();
			}
		}));
		bstats.addCustomChart(new BStatsLink.SingleLineChart("amount_holos_total", new Callable<Integer>() {
			@Override
			public Integer call() {
				return getHoloManager().getHolos().size();
			}
		}));
		bstats.addCustomChart(new BStatsLink.SingleLineChart("amount_holo_rows_total", new Callable<Integer>() {
			@Override
			public Integer call() {
				int i = 0;
				for(Holo h : getHoloManager().getHolos().values()) i += h.getContent().size();
				return i;
			}
		}));
	}

	public void onLoad() {
		GPM = this;
		saveDefaultConfig();
		cmanager = new CManager(getInstance());
		values = new Values();
		animationmanager = new AnimationManager(getInstance());
		holoconditionmanager = new HoloConditionManager(getInstance());
		holoimportmanager = new HoloImportManager(getInstance());
		umanager = new UManager(getInstance(), RESOURCE);
		mmanager = new MManager(getInstance());
		formatutil = new FormatUtil(getInstance());
	}

	public void onEnable() {
		if(!versionCheck()) return;
		holomanager = new HoloManager(getInstance());
		getCommand("gholo").setExecutor(new GHoloCommand(getInstance()));
		getCommand("gholo").setTabCompleter(new GHoloTabCompleter(getInstance()));
		getCommand("gholoreload").setExecutor(new GHoloReloadCommand(getInstance()));
		getServer().getPluginManager().registerEvents(new PlayerEvents(getInstance()), getInstance());
		setupSettings();
		linkBStats();
		getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-enabled");
		loadPluginDepends(Bukkit.getConsoleSender());
		updateCheck();
	}

	public void onDisable() {
		getHoloManager().saveHolos();
		getAnimationManager().stopHoloAnimations();
		getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-disabled");
	}

	private void loadPluginDepends(CommandSender s) {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			getValues().setPAPI(true);
			getMManager().sendMessage(s, "Plugin.plugin-hook", "%Link%", "PlaceholderAPI");
		} else getValues().setPAPI(false);
	}

	public void copyLangFiles() { for(String l : Arrays.asList("de_de", "en_en")) if(!new File("plugins/" + NAME + "/" + Values.LANG_PATH + "/" + l + Values.YML_FILETYP).exists()) saveResource(Values.LANG_PATH + "/" + l + Values.YML_FILETYP, false); }

	public void reload(CommandSender s) {
		getHoloManager().saveHolos();
		getAnimationManager().stopHoloAnimations();
		reloadConfig();
		getCManager().reload();
		setupSettings();
		loadPluginDepends(s);
		updateCheck();
	}

	private void updateCheck() {
		if(getCManager().CHECK_FOR_UPDATES) {
			getUManager().checkVersion();
			if(!getUManager().isLatestVersion()) {
				String me = getMManager().getMessage("Plugin.plugin-update", "%Name%", NAME, "%NewVersion%", getUManager().getLatestVersion(), "%Version%", getUManager().getPluginVersion(), "%Path%", getDescription().getWebsite());
				for(Player p : Bukkit.getOnlinePlayers()) if(p.hasPermission(NAME + ".Update") || p.hasPermission(NAME + ".*")) p.sendMessage(me);
				Bukkit.getConsoleSender().sendMessage(me);
			}
		}
	}

	private boolean versionCheck() {
		List<String> version_list = new ArrayList<>(); {
			version_list.add("v1_17_R1");
			version_list.add("v1_18_R1");
		}
		String v = Bukkit.getServer().getClass().getPackage().getName();
		v = v.substring(v.lastIndexOf('.') + 1);
		if(!NMSManager.isNMSCompatible() || !NMSManager.isNewerOrVersion(9, 0) || (NMSManager.isNewerOrVersion(17, 0) && !version_list.contains(v))) {
			getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-version", "%Version%", v);
			updateCheck();
			Bukkit.getPluginManager().disablePlugin(getInstance());
			return false;
		}
		return true;
	}

}