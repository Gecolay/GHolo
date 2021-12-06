package dev.geco.gholo.mcv.v1_17_R1.manager;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.NMSManager;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.values.Values;

public class HoloSpawnManager implements IHoloSpawnManager {
	
	private final GHoloMain GPM;
	
    public HoloSpawnManager(GHoloMain GHoloMain) { GPM = GHoloMain; }
    
    private HashMap<Holo, List<HoloRow>> holos = new HashMap<>();
    
    public void register() { for(Holo h : GPM.getHoloManager().getHolos().values()) registerHolo(h); }
    
    public void registerHolo(Holo H) {
    	
    	unregisterHolo(H, true);
    	
    	try {
    		
    		List<HoloRow> z = new ArrayList<>();
    		
        	Level w = (Level) H.getLocation().getWorld().getClass().getMethod("getHandle").invoke(H.getLocation().getWorld());
        	
        	double hh = 0;
        	
        	for(int i = 0; i < H.getContent().size(); i++) {
        		
        		String r = H.getContent().get(i);
        		
        		boolean u = StringUtils.countMatches(r, "%") > 1;
        		
        		Entity c = null;
        		
        		if(GPM.getCManager().USE_ARMOR_STANDS) {
        			
        			ArmorStand cp = new ArmorStand(w, 0, 0, 0);
            		
        			cp.setInvisible(true);
        			cp.setSmall(true);
        			cp.setNoGravity(true);
        			cp.setMarker(true);
        			cp.setNoBasePlate(true);
            		
            		c = cp;
        			
        		} else {
        			
        			AreaEffectCloud cp = new AreaEffectCloud(w, 0, 0, 0);
            		
        			cp.setRadius(0);
            		cp.setWaitTime(0);
            		cp.setDuration(Integer.MAX_VALUE);
            		
            		c = cp;
        			
        		}
        		
        		Entity o = null;
        		
        		Entity q = null;
        		
        		try {
        			
        			if(r.startsWith(Values.ICON_LINE_TEXT)) {
        				
        				String[] lc = r.replace(Values.ICON_LINE_TEXT, "").split(" ", 2);
            			
            			Material m = Material.valueOf(lc[0].toUpperCase());
            			
            			net.minecraft.world.item.ItemStack it = CraftItemStack.asNMSCopy(new ItemStack(m));
            			
            			if(lc.length > 1) try { it.setTag(TagParser.parseTag(lc[1])); } catch(Error | Exception e1) { }

            			o = new ItemEntity(w, H.getLocation().getX(), H.getLocation().getY() - hh, H.getLocation().getZ(), it);
            			
            			u = false;

            		} else if(r.startsWith(Values.ENTITY_LINE_TEXT)) {
            			
            			String[] lc = r.replace(Values.ENTITY_LINE_TEXT, "").split(" ", 2);

            			Optional<EntityType<?>> m = EntityType.byString(lc[0].toLowerCase());
            			
            			if(m.isPresent()) {
            				
            				q = m.get().create(w);
            				
            				if(lc.length > 1) try { q.load(TagParser.parseTag(lc[1])); } catch(Error | Exception e1) { }
            				
            				q.setNoGravity(true);
            				q.setPos(H.getLocation().getX(), H.getLocation().getY() - hh, H.getLocation().getZ());
            				
            				u = true;
            				
            			}
            			
            		}
        			
        		} catch(IllegalArgumentException e) { }
        		
        		c.setPos(H.getLocation().getX(), H.getLocation().getY() - hh - (GPM.getCManager().USE_ARMOR_STANDS ? 0 : 0.5) - (o == null ? 0.08 : 0), H.getLocation().getZ());
				
        		z.add(new HoloRow(i, u, c, o, q));
        		
        		hh += GPM.getCManager().SPACE_BETWEEN_LINES;
        		
        	}
        	
        	H.setMidLocation(H.getLocation().clone().subtract(0, hh / 2, 0));
        	
        	H.clearUUIDs();
        	
        	holos.put(H, z);
    		
    	} catch(Exception e) {
    		
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void unregister() {
    	
    	Iterator<Holo> z = holos.keySet().iterator();
    	
    	while(z.hasNext()) {
    		unregisterHolo(z.next(), false);
    		z.remove();
    	}
    	
    }
    
    public void unregisterHolo(Holo H, boolean re) {
    	
    	if(holos.containsKey(H)) {
    		
    		for(HoloRow sh : holos.get(H)) {

				ClientboundRemoveEntityPacket r = new ClientboundRemoveEntityPacket(sh.getBase().getId());

				ClientboundRemoveEntityPacket r2 = sh.getTop() != null ? new ClientboundRemoveEntityPacket(sh.getTop().getId()) : null;
    			
    			for(UUID u : H.getUUIDs()) {
    				
    				Player t = Bukkit.getPlayer(u);
    				
    				if(t != null && t.isOnline()) {

						ServerPlayer tc = (ServerPlayer) NMSManager.getNMSCopy(t);
    					
    					if(r2 != null) tc.connection.send(r2);

						tc.connection.send(r);
    					
    				}
    				
    			}
    			
    		}
    		
        	if(re) holos.remove(H);
        	
    	}
    
    }
    
    private Set<Player> getNearPlayers(Location L, double R) {
        Set<Player> pl = new HashSet<>();
        L.getWorld().getPlayers().stream().filter(o -> L.distance(o.getLocation()) <= R).forEach(pl::add);
        return pl;
    }
    
    private Set<Player> getNearPlayers(Location L, double R, String Condition) {
        Set<Player> pl = new HashSet<>();
        L.getWorld().getPlayers().stream().filter(o -> L.distance(o.getLocation()) <= R && GPM.getHoloConditionManager().checkCondition(Condition, o)).forEach(pl::add);
        return pl;
    }
    
    public void spawn() {
    	
    	try {
    		
    		for(Entry<Holo, List<HoloRow>> h : holos.entrySet()) {
        		
        		Holo holo = h.getKey();
        		
        		if(holo.getRange() == 0) continue;
        		
        		int ra = holo.getRange() == -1 ? GPM.getCManager().USE_ARMOR_STANDS ? 64 : 40 : holo.getRange();
        		
        		Set<Player> o = (holo.getCondition() != null ? getNearPlayers(holo.getMidLocation(), ra, holo.getCondition()) : getNearPlayers(holo.getMidLocation(), ra));
        		
        		List<Player> rl = new ArrayList<>();
        		
        		for(UUID u : holo.getUUIDs()) {
    				
    				Player t = Bukkit.getPlayer(u);
    				
    				if(t != null && !o.contains(t)) rl.add(t);
    				
    			}
        		
        		for(HoloRow sh : h.getValue()) {
        			
        			String row = holo.getContent().get(sh.getRow());
        			
        			for(Player t : o) {

        				if(!t.isOnline()) continue;

						ServerPlayer tc = (ServerPlayer) NMSManager.getNMSCopy(t);

        				if(!holo.getUUIDs().contains(t.getUniqueId())) {
        					
        					String text = GPM.getFormatUtil().formatSymbols(row);
        					
        					if(sh.needUpdate()) text = GPM.getFormatUtil().formatPlaceholders(text, t);
        					
        					text = GPM.getMManager().getGradientMessage(text);
        					
        					sh.getBase().setCustomNameVisible(sh.getTop() == null && !text.equalsIgnoreCase(Values.EMPTY_LINE_TEXT));

        					try { sh.getBase().setCustomName(CraftChatMessage.fromString(text)[0]); } catch (Exception e) { }

							ClientboundAddEntityPacket pa = new ClientboundAddEntityPacket(sh.getBase());
        					
        					tc.connection.send(pa);

							ClientboundSetEntityDataPacket pa2 = new ClientboundSetEntityDataPacket(sh.getBase().getId(), sh.getBase().getEntityData(), true);

							tc.connection.send(pa2);
        					
        					if(sh.getTop() != null) {

								ClientboundAddEntityPacket pa3 = new ClientboundAddEntityPacket(sh.getTop());

								tc.connection.send(pa3);

								ClientboundSetEntityDataPacket pa4 = new ClientboundSetEntityDataPacket(sh.getTop().getId(), sh.getTop().getEntityData(), true);

								tc.connection.send(pa4);
            					
            					if(sh.getItem() != null) {
            						
            						sh.getItem().startRiding(sh.getBase());

									ClientboundSetPassengersPacket pa5 = new ClientboundSetPassengersPacket(sh.getBase());

									tc.connection.send(pa5);
            						
            					}
        						
        					}
        					
        				} else if(sh.needUpdate()) {
        					
        					if(sh.getTop() == null) {
        						
        						String text = GPM.getFormatUtil().formatSymbols(row);
            					
            					text = GPM.getFormatUtil().formatPlaceholders(text, t);
            					
            					text = GPM.getMManager().getGradientMessage(text);
            					
            					sh.getBase().setCustomNameVisible(sh.getTop() == null && !text.equalsIgnoreCase(Values.EMPTY_LINE_TEXT));
            					
            					try { sh.getBase().setCustomName(CraftChatMessage.fromString(text)[0]); } catch (Exception e) { }

								ClientboundSetEntityDataPacket pa2 = new ClientboundSetEntityDataPacket(sh.getBase().getId(), sh.getBase().getEntityData(), true);

								tc.connection.send(pa2);
        						
        					} else if(sh.getEntity() != null) {
        						
        						Vector v = t.getLocation().subtract(sh.getEntity().getBukkitEntity().getLocation()).toVector();
        						
        						Location l = sh.getEntity().getBukkitEntity().getLocation().setDirection(v);
        						
        						byte u = getFixedRotation(l.getYaw());
            					byte u1 = getFixedRotation(l.getPitch());

								ClientboundRotateHeadPacket pa6 = new ClientboundRotateHeadPacket(sh.getEntity(), u);

								tc.connection.send(pa6);

								ClientboundMoveEntityPacket.PosRot pa7 = new ClientboundMoveEntityPacket.PosRot(sh.getEntity().getId(), (short) 0, (short) 0, (short) 0, u, u1, true);

								tc.connection.send(pa7);
            					
        					}
        					
        				}
        				
        			}
        			
        			if(rl.size() > 0) {

						ClientboundRemoveEntityPacket r = new ClientboundRemoveEntityPacket(sh.getBase().getId());

						ClientboundRemoveEntityPacket r2 = sh.getTop() != null ? new ClientboundRemoveEntityPacket(sh.getTop().getId()) : null;
            			
            			for(Player t : rl) {

							ServerPlayer tc = (ServerPlayer) NMSManager.getNMSCopy(t);

            				if(r2 != null) tc.connection.send(r2);

							tc.connection.send(r2);
            				
            			}
            			
        			}
        			
        		}
        		
        		holo.clearUUIDs();
        		
        		for(Player t : o) holo.addUUID(t.getUniqueId());
        		
        	}
    		
    	} catch(ConcurrentModificationException e) { } catch(Exception e) { e.printStackTrace(); }
    	
    }
    
    private byte getFixedRotation(float Y) { return (byte) (Y * 256.0F / 360.0F); }
    
    private static class HoloRow {
    	
    	private final int r;
    	
    	private final boolean u;
    	
    	private final Entity c;
    	
    	private final Entity i;
    	
    	private final Entity e;
    	
    	public HoloRow(int R, boolean U, Entity C, Entity I, Entity E) {
    		r = R;
    		u = U;
    		c = C;
    		i = I;
    		e = E;
    	}
    	
    	public int getRow() { return r; }
    	
    	public boolean needUpdate() { return u; }
    	
    	public Entity getBase() { return c; }
    	
    	public Entity getItem() { return i; }
    	
    	public Entity getEntity() { return e; }
    	
    	public Entity getTop() { return i != null ? i : e; }
    	
    }
    
}