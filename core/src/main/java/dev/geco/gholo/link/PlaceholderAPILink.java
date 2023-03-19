package dev.geco.gholo.link;

import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.*;

import me.clip.placeholderapi.expansion.*;

import dev.geco.gholo.GHoloMain;

public class PlaceholderAPILink extends PlaceholderExpansion {

    private final GHoloMain GPM;

    public PlaceholderAPILink(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean canRegister() { return GPM.isEnabled(); }

    @Override
    public @NotNull String getName() { return GPM.getDescription().getName(); }

    @Override
    public @NotNull String getIdentifier() { return GPM.NAME.toLowerCase(); }

    @Override
    public @NotNull String getAuthor() { return GPM.getDescription().getAuthors().toString(); }

    @Override
    public @NotNull String getVersion() { return GPM.getDescription().getVersion(); }

    @Override
    public @NotNull List<String> getPlaceholders() { return Arrays.asList(); }

    @Override
    public String onRequest(OfflinePlayer Player, @NotNull String Params) {

        return null;
    }

}