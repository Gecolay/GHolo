package dev.geco.gholo;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import dev.geco.gholo.api.event.*;
import dev.geco.gholo.cmd.*;
import dev.geco.gholo.cmd.tab.*;
import dev.geco.gholo.events.*;
import dev.geco.gholo.link.*;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.manager.mm.*;
import dev.geco.gholo.util.*;

public class GHoloMain extends JavaPlugin {

    private SVManager svManager;
    public SVManager getSVManager() { return svManager; }

    private CManager cManager;
    public CManager getCManager() { return cManager; }

    private DManager dManager;
    public DManager getDManager() { return dManager; }

    private HoloManager holoManager;
    public HoloManager getHoloManager() { return holoManager; }

    private IHoloSpawnManager holoSpawnManager;
    public IHoloSpawnManager getHoloSpawnManager() { return holoSpawnManager; }

    private HoloAnimationManager holoAnimationManager;
    public HoloAnimationManager getHoloAnimationManager() { return holoAnimationManager; }

    private HoloImportManager holoImportManager;
    public HoloImportManager getHoloImportManager() { return holoImportManager; }

    private UManager uManager;
    public UManager getUManager() { return uManager; }

    private PManager pManager;
    public PManager getPManager() { return pManager; }

    private TManager tManager;
    public TManager getTManager() { return tManager; }

    private MManager mManager;
    public MManager getMManager() { return mManager; }

    private FormatUtil formatUtil;
    public FormatUtil getFormatUtil() { return formatUtil; }

    private boolean placeholderAPILink;
    public boolean getPlaceholderAPILink() { return placeholderAPILink; }

    private boolean spigotBased = false;
    public boolean isSpigotBased() { return spigotBased; }

    private boolean basicPaperBased = false;
    public boolean isBasicPaperBased() { return basicPaperBased; }

    private boolean paperBased = false;
    public boolean isPaperBased() { return paperBased; }

    public final String NAME = "GHolo";

    public final String RESOURCE = "000000";

    private static GHoloMain GPM;

    public static GHoloMain getInstance() { return GPM; }

    private void loadSettings(CommandSender Sender) {

        if(!connectDatabase(Sender)) return;

        getHoloManager().createTable();
        ImageUtil.generateFolder();

        getHoloAnimationManager().loadHoloAnimations();
        getHoloManager().loadHolos();
    }

    private void linkBStats() {

        BStatsLink bstats = new BStatsLink(getInstance(), 4914);

        bstats.addCustomChart(new BStatsLink.SimplePie("plugin_language", () -> getCManager().L_LANG));
    }

    public void onLoad() {

        GPM = this;

        svManager = new SVManager(getInstance());
        dManager = new DManager(getInstance());
        cManager = new CManager(getInstance());
        uManager = new UManager(getInstance());
        pManager = new PManager(getInstance());
        tManager = new TManager(getInstance());
        holoManager = new HoloManager(getInstance());
        holoAnimationManager = new HoloAnimationManager(getInstance());
        holoImportManager = new HoloImportManager(getInstance());

        formatUtil = new FormatUtil(getInstance());

        preloadPluginDependencies();

        mManager = isBasicPaperBased() && GPM.getSVManager().isNewerOrVersion(18, 2) ? new PMManager(getInstance()) : new SMManager(getInstance());
    }

    public void onEnable() {

        loadSettings(Bukkit.getConsoleSender());
        if(!versionCheck()) return;

        holoSpawnManager = (IHoloSpawnManager) GPM.getSVManager().getPackageObject("manager.HoloSpawnManager", getInstance());

        setupCommands();
        setupEvents();
        linkBStats();

        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-enabled");

        loadPluginDependencies(Bukkit.getConsoleSender());
        GPM.getUManager().checkForUpdates();
    }

    public void onDisable() {

        getDManager().close();
        getHoloAnimationManager().stopHoloAnimations();
        getHoloManager().unloadHolos();

        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-disabled");
    }

    private void setupCommands() {

        getCommand("gholo").setExecutor(new GHoloCommand(getInstance()));
        getCommand("gholo").setTabCompleter(new GHoloTabComplete(getInstance()));
        getCommand("gholo").setPermissionMessage(getMManager().getMessage("Messages.command-permission-error"));
        getCommand("gholoreload").setExecutor(new GHoloReloadCommand(getInstance()));
        getCommand("gholoreload").setTabCompleter(new EmptyTabComplete());
        getCommand("gholoreload").setPermissionMessage(getMManager().getMessage("Messages.command-permission-error"));
    }

    private void setupEvents() {

        getServer().getPluginManager().registerEvents(new PlayerEvents(getInstance()), getInstance());
    }

    private void preloadPluginDependencies() {

        try {
            Class.forName("org.spigotmc.event.entity.EntityDismountEvent");
            spigotBased = true;
        } catch (ClassNotFoundException ignored) { }

        try {
            Class.forName("io.papermc.paper.event.entity.EntityMoveEvent");
            basicPaperBased = true;
        } catch (ClassNotFoundException ignored) { }

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            paperBased = true;
        } catch (ClassNotFoundException ignored) { }
    }

    private void loadPluginDependencies(CommandSender Sender) {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        if(plugin != null && plugin.isEnabled()) {
            placeholderAPILink = true;
            getMManager().sendMessage(Sender, "Plugin.plugin-link", "%Link%", plugin.getName());
        } else placeholderAPILink = false;
    }

    public void reload(CommandSender Sender) {

        Bukkit.getPluginManager().callEvent(new GHoloReloadEvent(getInstance()));

        getCManager().reload();
        getMManager().loadMessages();

        getDManager().close();
        getHoloAnimationManager().stopHoloAnimations();
        getHoloManager().unloadHolos();

        loadSettings(Sender);
        loadPluginDependencies(Sender);
        GPM.getUManager().checkForUpdates();
    }

    private boolean connectDatabase(CommandSender Sender) {

        boolean connect = getDManager().connect();

        if(connect) return true;

        getMManager().sendMessage(Sender, "Plugin.plugin-data");

        Bukkit.getPluginManager().disablePlugin(getInstance());

        return false;
    }

    private boolean versionCheck() {

        if(!getSVManager().hasPackageClass("manager.HoloSpawnManager")) {

            String version = Bukkit.getServer().getClass().getPackage().getName();

            getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-version", "%Version%", version.substring(version.lastIndexOf('.') + 1));

            GPM.getUManager().checkForUpdates();

            Bukkit.getPluginManager().disablePlugin(getInstance());

            return false;
        }

        return true;
    }

}