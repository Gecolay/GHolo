package dev.geco.gholo;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.java.*;

import dev.geco.gholo.api.event.*;
import dev.geco.gholo.cmd.*;
import dev.geco.gholo.cmd.tab.*;
import dev.geco.gholo.events.*;
import dev.geco.gholo.link.*;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.util.*;

public class GHoloMain extends JavaPlugin {

    private CManager cManager;
    public CManager getCManager() { return cManager; }

    private DManager dManager;
    public DManager getDManager() { return dManager; }

    private HoloAnimationManager holoAnimationManager;
    public HoloAnimationManager getHoloAnimationManager() { return holoAnimationManager; }

    private HoloImportManager holoImportManager;
    public HoloImportManager getHoloImportManager() { return holoImportManager; }

    private HoloManager holoManager;
    public HoloManager getHoloManager() { return holoManager; }

    private UManager uManager;
    public UManager getUManager() { return uManager; }

    private PManager pManager;
    public PManager getPManager() { return pManager; }

    private MManager mManager;
    public MManager getMManager() { return mManager; }

    private FormatUtil formatUtil;
    public FormatUtil getFormatUtil() { return formatUtil; }

    private PlaceholderAPILink placeholderAPILink;
    public PlaceholderAPILink getPlaceholderAPILink() { return placeholderAPILink; }

    public final String NAME = "GHolo";

    public final String RESOURCE = "";

    private static GHoloMain GPM;

    public static GHoloMain getInstance() { return GPM; }

    private void loadSettings() {

        dManager.connect();

        getHoloManager().createTable();

        getHoloAnimationManager().loadHoloAnimations();
        getHoloManager().loadHolos();
    }

    private void linkBStats() {

        BStatsLink bstats = new BStatsLink(getInstance(), 4914);

        bstats.addCustomChart(new BStatsLink.SimplePie("plugin_language", () -> getCManager().L_LANG));
    }

    public void onLoad() {

        GPM = this;

        dManager = new DManager(getInstance());
        cManager = new CManager(getInstance());
        uManager = new UManager(getInstance());
        pManager = new PManager(getInstance());
        mManager = new MManager(getInstance());
        holoAnimationManager = new HoloAnimationManager(getInstance());
        holoImportManager = new HoloImportManager(getInstance());

        formatUtil = new FormatUtil(getInstance());
    }

    public void onEnable() {

        if(!versionCheck()) return;

        holoManager = new HoloManager(getInstance());

        loadSettings();

        setupCommands();
        setupEvents();
        linkBStats();

        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-enabled");

        loadPluginDependencies(Bukkit.getConsoleSender());
        GPM.getUManager().checkForUpdates();
    }

    public void onDisable() {

        dManager.close();
        getHoloAnimationManager().stopHoloAnimations();
        getHoloManager().unloadHolos();

        if(getPlaceholderAPILink() != null) getPlaceholderAPILink().unregister();

        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-disabled");
    }

    private void setupCommands() {

        getCommand("gholo").setExecutor(new GHoloCommand(getInstance()));
        getCommand("gholo").setTabCompleter(new GHoloTabComplete(getInstance()));
        getCommand("gholoreload").setExecutor(new GHoloReloadCommand(getInstance()));
        getCommand("gholoreload").setTabCompleter(new EmptyTabComplete());
    }

    private void setupEvents() {

        getServer().getPluginManager().registerEvents(new PlayerEvents(getInstance()), getInstance());
    }

    private void preloadPluginDependencies() { }

    private void loadPluginDependencies(CommandSender Sender) {

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPILink = new PlaceholderAPILink(getInstance());
            getMManager().sendMessage(Sender, "Plugin.plugin-link", "%Link%", "PlaceholderAPI");
            getPlaceholderAPILink().register();
        } else placeholderAPILink = null;
    }

    public void reload(CommandSender Sender) {

        Bukkit.getPluginManager().callEvent(new GHoloReloadEvent(getInstance()));

        getCManager().reload();
        getMManager().loadMessages();

        dManager.close();
        getHoloAnimationManager().stopHoloAnimations();
        getHoloManager().unloadHolos();

        if(getPlaceholderAPILink() != null) getPlaceholderAPILink().unregister();

        loadSettings();
        loadPluginDependencies(Sender);
        GPM.getUManager().checkForUpdates();
    }

    private boolean versionCheck() {

        if(!NMSManager.hasPackageClass("manager.HoloSpawnManager")) {

            String version = Bukkit.getServer().getClass().getPackage().getName();

            getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-version", "%Version%", version.substring(version.lastIndexOf('.') + 1));

            GPM.getUManager().checkForUpdates();

            Bukkit.getPluginManager().disablePlugin(getInstance());

            return false;
        }

        return true;
    }

}