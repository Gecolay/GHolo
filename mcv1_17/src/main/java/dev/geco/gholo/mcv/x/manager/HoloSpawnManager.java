package dev.geco.gholo.mcv.x.manager;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.atomic.*;

import com.mojang.datafixers.util.*;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.*;
import org.bukkit.craftbukkit.v1_17_R1.util.*;
import org.bukkit.entity.*;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.Entity;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.mcv.x.objects.*;
import dev.geco.gholo.objects.*;

public class HoloSpawnManager implements IHoloSpawnManager {

    private final GHoloMain GPM;

    private static AtomicInteger ENTITY_COUNTER;

    private Field addEntityIdField;

    public HoloSpawnManager(GHoloMain GHoloMain) {
        GPM = GHoloMain;
        try {
            for(Field field : Entity.class.getDeclaredFields()) if(field.getType().equals(AtomicInteger.class)) {
                field.setAccessible(true);
                ENTITY_COUNTER = (AtomicInteger) field.get(null);
            }
            for(Field field : ClientboundAddEntityPacket.class.getDeclaredFields()) if(field.getType().equals(int.class)) {
                field.setAccessible(true);
                addEntityIdField = field;
                break;
            }
        } catch (Exception e) { e.printStackTrace(); }
        ENTITY_COUNTER = new AtomicInteger();
    }

    private final HashMap<GHolo, List<Entity>> holos = new HashMap<>();

    private final HashMap<String, HashMap<Integer, Pair<Integer, String>>> cache = new HashMap<>();

    private void addHoloRowEntity(GHolo Holo, GHoloRow HoloRow, int CurrentSize) {

        double height = HoloRow.getHeight() * CurrentSize;

        HoloEntity holoEntity = new HoloEntity(Holo.getLocation());

        holoEntity.setPos(Holo.getLocation().getX(), Holo.getLocation().getY() - height - 0.08, Holo.getLocation().getZ());

        holos.get(Holo).add(holoEntity);
    }

    private void updateMidLocation(GHolo Holo) {

        double height = 0;

        for(GHoloRow holoRow : Holo.getRows()) height += holoRow.getHeight();

        Holo.setMidLocation(Holo.getLocation().clone().subtract(0, height / 2, 0));
    }

    public void registerHolo(GHolo Holo) {

        unregisterHolo(Holo);

        try {

            holos.put(Holo, new ArrayList<>());

            int size = 0;

            for(GHoloRow holoRow : Holo.getRows()) {
                addHoloRowEntity(Holo, holoRow, size);
                size++;
            }

            updateMidLocation(Holo);
        } catch (Throwable e) { e.printStackTrace(); }
    }

    private void removeForPlayer(Player Player, List<Entity> Entities) {

        if(!Player.isOnline()) return;

        String playerUUID = Player.getUniqueId().toString();

        if(!cache.containsKey(playerUUID)) return;

        ServerPlayer serverPlayer = ((CraftPlayer) Player).getHandle();

        for(Entity holoRow : Entities) {

            cache.get(playerUUID).remove(holoRow.getId());
            serverPlayer.connection.send(new ClientboundRemoveEntityPacket(cache.get(playerUUID).get(holoRow.getId()).getFirst()));
        }
    }

    public void updateHolo(GHolo Holo) {

        int holoSize = holos.get(Holo).size();

        if(Holo.getRows().size() == holoSize) return;

        if(Holo.getRows().size() > holoSize) {

            addHoloRowEntity(Holo, Holo.getRows().get(Holo.getRows().size() - 1), holoSize);
            updateMidLocation(Holo);
            return;
        }

        List<Entity> entities = Collections.singletonList(holos.get(Holo).get(holoSize - 1));
        for(Player player : Holo.getPlayers()) removeForPlayer(player, entities);

        holos.get(Holo).remove(holoSize - 1);
    }

    public void unregisterHolo(GHolo Holo) { unregisterHolo(Holo, true); }

    public void unregisterHolo(GHolo Holo, boolean Remove) {

        if(!holos.containsKey(Holo)) return;

        for(Player player : Holo.getPlayers()) removeForPlayer(player, holos.get(Holo));

        Holo.clearPlayers();

        if(Remove) holos.remove(Holo);
    }

    private Set<Player> getNearPlayers(Location Location, double Range) {
        Set<Player> players = new HashSet<>();
        Objects.requireNonNull(Location.getWorld()).getPlayers().stream().filter(player -> Location.distance(player.getLocation()) <= Range).forEach(players::add);
        return players;
    }

    public void clearPlayerCache(Player Player) { cache.remove(Player.getUniqueId().toString()); }

    public void spawn() {

        try {

            Iterator<Entry<GHolo, List<Entity>>> holoIterator = holos.entrySet().iterator();

            while(holoIterator.hasNext()) {

                Entry<GHolo, List<Entity>> holoEntry = holoIterator.next();

                GHolo holo = holoEntry.getKey();

                if(holo.getRange() == 0) continue;

                int range = holo.getRange() < 0 ? HoloManager.MAX_HOLO_RANGE : holo.getRange();

                Set<Player> players = getNearPlayers(holo.getMidLocation(), range);

                List<Player> despawnPlayers = new ArrayList<>();

                for(Player player : holo.getPlayers()) if(!players.contains(player)) despawnPlayers.add(player);

                int row = 0;

                for(Entity holoRow : holoEntry.getValue()) {

                    String rowContent = holo.getRows().get(row).getContent();

                    int baseId = holoRow.getId();

                    row++;

                    for(Player player : players) {

                        if(!player.isOnline()) continue;

                        rowContent = GPM.getFormatUtil().formatSymbols(GPM.getFormatUtil().formatPlaceholders(rowContent, player));

                        String playerUUID = player.getUniqueId().toString();

                        if(!cache.containsKey(playerUUID)) cache.put(playerUUID, new HashMap<>());

                        HashMap<Integer, Pair<Integer, String>> cacheHolo = cache.get(playerUUID);

                        if(cacheHolo.containsKey(baseId) && rowContent.equals(cacheHolo.get(baseId).getSecond())) continue;

                        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

                        holoRow.setCustomNameVisible(!rowContent.equalsIgnoreCase(HoloManager.EMPTY_STRING));

                        try { holoRow.setCustomName(CraftChatMessage.fromString(rowContent)[0]); } catch (Exception e) { e.printStackTrace(); }

                        int id = cacheHolo.containsKey(baseId) ? cacheHolo.get(baseId).getFirst() : ENTITY_COUNTER.incrementAndGet();

                        if(!cacheHolo.containsKey(baseId)) {

                            ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(holoRow);
                            addEntityIdField.set(spawnPacket, id);
                            serverPlayer.connection.send(spawnPacket);
                        }

                        serverPlayer.connection.send(new ClientboundSetEntityDataPacket(holoRow.getId(), holoRow.getEntityData(), true));

                        cacheHolo.put(baseId, new Pair<>(id, rowContent));

                        cache.put(playerUUID, cacheHolo);
                    }
                }

                if(despawnPlayers.size() > 0) for(Player despawnPlayer : despawnPlayers) removeForPlayer(despawnPlayer, holoEntry.getValue());

                holo.setPlayers(new ArrayList<>(players));
            }
        } catch (Throwable e) { e.printStackTrace(); }
    }

}