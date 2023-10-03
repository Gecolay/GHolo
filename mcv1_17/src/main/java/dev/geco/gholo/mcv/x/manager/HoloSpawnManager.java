package dev.geco.gholo.mcv.x.manager;

import java.util.*;
import java.util.Map.*;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.*;
import org.bukkit.craftbukkit.v1_17_R1.util.*;
import org.bukkit.entity.*;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.mcv.x.objects.*;
import dev.geco.gholo.objects.*;

public class HoloSpawnManager implements IHoloSpawnManager {

    private final GHoloMain GPM;

    public HoloSpawnManager(GHoloMain GHoloMain) { GPM = GHoloMain; }

    private final HashMap<GHolo, List<GHoloRow>> holos = new HashMap<>();

    private final HashMap<UUID, HashMap<Integer, String>> cache = new HashMap<>();

    public void registerHolo(GHolo Holo) {

        unregisterHolo(Holo);

        try {

            List<GHoloRow> holoRows = new ArrayList<>();

            double height = 0;

            for(String content : Holo.getContent()) {

                boolean containsPlaceholder = content.chars().filter(ch -> ch == '%').count() > 1;

                HoloEntity holoEntity = new HoloEntity(Holo.getLocation());

                holoEntity.setPos(Holo.getLocation().getX(), Holo.getLocation().getY() - height - 0.08, Holo.getLocation().getZ());

                holoRows.add(new GHoloRow(containsPlaceholder, holoEntity));

                height += GPM.getCManager().SPACE_BETWEEN_LINES;
            }

            Holo.setMidLocation(Holo.getLocation().clone().subtract(0, height / 2, 0));

            Holo.clearUUIDs();

            holos.put(Holo, holoRows);
        } catch (Throwable e) { e.printStackTrace(); }
    }

    public void unregisterHolo(GHolo Holo) {

        unregisterHolo(Holo, true);
        cache.clear();
    }

    public void unregisterHolo(GHolo Holo, boolean Remove) {

        if(!holos.containsKey(Holo)) return;

        for(GHoloRow holoRow : holos.get(Holo)) {

            ClientboundRemoveEntityPacket removeEntitiesPacket = new ClientboundRemoveEntityPacket(holoRow.getBase().getId());

            for(UUID uuid : Holo.getUUIDs()) {

                Player player = Bukkit.getPlayer(uuid);

                if(player != null && player.isOnline()) ((CraftPlayer) player).getHandle().connection.send(removeEntitiesPacket);
            }
        }

        if(Remove) holos.remove(Holo);
    }

    private Set<Player> getNearPlayers(Location Location, double Range) {
        Set<Player> players = new HashSet<>();
        Location.getWorld().getPlayers().stream().filter(player -> Location.distance(player.getLocation()) <= Range).forEach(players::add);
        return players;
    }

    public void spawn() {

        try {

            for(Entry<GHolo, List<GHoloRow>> holoEntry : holos.entrySet()) {

                GHolo holo = holoEntry.getKey();

                if(holo.getRange() == 0) continue;

                int range = holo.getRange() < 0 ? 250 : holo.getRange();

                Set<Player> players = getNearPlayers(holo.getMidLocation(), range);

                List<Player> despawnPlayers = new ArrayList<>();

                for(UUID uuid : holo.getUUIDs()) {

                    Player player = Bukkit.getPlayer(uuid);

                    if(player != null && !players.contains(player)) despawnPlayers.add(player);
                }

                int row = 0;

                for(GHoloRow holoRow : holoEntry.getValue()) {

                    String rowContent = holo.getContent().get(row);

                    row++;

                    for(Player player : players) {

                        if(!player.isOnline()) continue;

                        if(holoRow.needUpdate()) rowContent = GPM.getFormatUtil().formatPlaceholders(rowContent, player);

                        UUID uuid = player.getUniqueId();

                        if(cache.containsKey(uuid) && rowContent.equals(cache.get(uuid).get(holoRow.getBase().getId()))) continue;

                        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

                        holoRow.getBase().setCustomNameVisible(!rowContent.equalsIgnoreCase("[]"));

                        try { holoRow.getBase().setCustomName(CraftChatMessage.fromString(rowContent)[0]); } catch (Exception e) { e.printStackTrace(); }

                        if(!holo.getUUIDs().contains(player.getUniqueId())) serverPlayer.connection.send(new ClientboundAddEntityPacket(holoRow.getBase()));

                        serverPlayer.connection.send(new ClientboundSetEntityDataPacket(holoRow.getBase().getId(), holoRow.getBase().getEntityData(), true));

                        HashMap<Integer, String> playerCache = cache.getOrDefault(uuid, new HashMap<>());

                        playerCache.put(holoRow.getBase().getId(), rowContent);

                        cache.put(uuid, playerCache);
                    }

                    if(despawnPlayers.size() > 0) {

                        ClientboundRemoveEntityPacket removeEntitiesPacket = new ClientboundRemoveEntityPacket(holoRow.getBase().getId());

                        for(Player despawnPlayer : despawnPlayers) ((CraftPlayer) despawnPlayer).getHandle().connection.send(removeEntitiesPacket);
                    }
                }

                holo.clearUUIDs();

                for(Player player : players) holo.addUUID(player.getUniqueId());
            }
        } catch (Throwable e) { e.printStackTrace(); }
    }

}