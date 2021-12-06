package dev.geco.gholo.manager;

import java.util.*;
import java.util.Map.Entry;

import dev.geco.gholo.objects.IHoloSpawnManager;
import org.apache.commons.lang.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.Holo;
import dev.geco.gholo.values.Values;

public class HoloSpawnManager implements IHoloSpawnManager {
	
	private final GHoloMain GPM;
	
	public HoloSpawnManager(GHoloMain GHoloMain) { GPM = GHoloMain; }
	
	private HashMap<Holo, List<HoloRow>> holos = new HashMap<>();
	
	private Class<?> World = NMSManager.getNMSClass("World");
	private Class<?> Entity = NMSManager.getNMSClass("Entity");
	private Class<?> DataWatcher = NMSManager.getNMSClass("DataWatcher");
	private Class<?> MojangsonParser = NMSManager.getNMSClass("MojangsonParser");
	private Class<?> NBTTagCompound = NMSManager.getNMSClass("NBTTagCompound");
	private Class<?> EntityArmorStand = NMSManager.getNMSClass("EntityArmorStand");
	private Class<?> EntityAreaEffectCloud = NMSManager.getNMSClass("EntityAreaEffectCloud");
	private Class<?> EntityItem = NMSManager.getNMSClass("EntityItem");
	private Class<?> ItemStack = NMSManager.getNMSClass("ItemStack");
	private Class<?> CraftItemStack = NMSManager.getOBCClass("inventory.CraftItemStack");
	private Class<?> IChatBaseComponent = NMSManager.getNMSClass("IChatBaseComponent");
	private Class<?> CraftChatMessage = NMSManager.getOBCClass("util.CraftChatMessage");
	private Class<?> PacketPlayOutEntityDestroy = NMSManager.getNMSClass("PacketPlayOutEntityDestroy");
	private Class<?> PacketPlayOutSpawnEntity = NMSManager.getNMSClass("PacketPlayOutSpawnEntity");
	private Class<?> PacketPlayOutEntityMetadata = NMSManager.getNMSClass("PacketPlayOutEntityMetadata");
	private Class<?> PacketPlayOutMount = NMSManager.getNMSClass("PacketPlayOutMount");
	
	public void register() { for(Holo h : GPM.getHoloManager().getHolos().values()) registerHolo(h); }
	
	public void registerHolo(Holo H) {
		
		unregisterHolo(H, true);
		
		try {
			
			List<HoloRow> z = new ArrayList<>();
			
			Object w = H.getLocation().getWorld().getClass().getMethod("getHandle").invoke(H.getLocation().getWorld());
			
			double hh = 0;
			
			for(int i = 0; i < H.getContent().size(); i++) {
				
				String r = H.getContent().get(i);
				
				boolean u = StringUtils.countMatches(r, "%") > 1;
				
				Object c = null;
				
				if(GPM.getCManager().USE_ARMOR_STANDS || !NMSManager.isNewerOrVersion(14, 0)) {
					
					Object cp = EntityArmorStand.getConstructor(World, double.class, double.class, double.class).newInstance(w, 0, 0, 0);
					
					try { NMSManager.getMethod("setInvisible", EntityArmorStand, boolean.class).invoke(cp, true); } catch(NullPointerException e) { }
					try { NMSManager.getMethod("setSmall", EntityArmorStand, boolean.class).invoke(cp, true); } catch(NullPointerException e) { }
					try { NMSManager.getMethod("setNoGravity", EntityArmorStand, boolean.class).invoke(cp, true); } catch(NullPointerException e) { }
					try { NMSManager.getMethod("setMarker", EntityArmorStand, boolean.class).invoke(cp, true); } catch(NullPointerException e) { }
					try { NMSManager.getMethod("setBasePlate", EntityArmorStand, boolean.class).invoke(cp, true); } catch(NullPointerException e) { }
					
					c = cp;
					
				} else {
					
					Object cp = EntityAreaEffectCloud.getConstructor(World, double.class, double.class, double.class).newInstance(w, 0, 0, 0);
					
					try { NMSManager.getMethod("setRadius", EntityAreaEffectCloud, float.class).invoke(cp, 0); } catch(NullPointerException e) { }
					try { NMSManager.getMethod("setWaitTime", EntityAreaEffectCloud, int.class).invoke(cp, 0); } catch(NullPointerException e) { }
					try { NMSManager.getMethod("setDuration", EntityAreaEffectCloud, int.class).invoke(cp, Integer.MAX_VALUE); } catch(NullPointerException e) { }
					
					c = cp;
					
				}
				
				Object o = null;
				
				Object q = null;
				
				try {
					
					if(r.startsWith(Values.ICON_LINE_TEXT)) {
						
						String[] lc = r.replace(Values.ICON_LINE_TEXT, "").split(" ", 2);
						
						Material m = Material.valueOf(lc[0].toUpperCase());
						
						Object it = CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, new ItemStack(m));
						
						if(lc.length > 1) try { NMSManager.getMethod("setTag", ItemStack, NBTTagCompound).invoke(it, NBTTagCompound.cast(MojangsonParser.getMethod("parse", String.class).invoke(null, lc[1]))); } catch(Error | Exception e1) { }
						
						o = EntityItem.getConstructor(World, double.class, double.class, double.class, it.getClass()).newInstance(w, H.getLocation().getX(), H.getLocation().getY() - hh, H.getLocation().getZ(), it);
						
						u = false;
						
					} else if(r.startsWith(Values.ENTITY_LINE_TEXT)) {
						
						r.replace(Values.ENTITY_LINE_TEXT, "").split(" ", 2);
						
						
						
					}
					
				} catch(IllegalArgumentException e) { }
				
				if(o == null && !r.equalsIgnoreCase(Values.EMPTY_LINE_TEXT)) try { NMSManager.getMethod("setCustomNameVisible", Entity, boolean.class).invoke(c, true); } catch(NullPointerException e) { }
				try { NMSManager.getMethod("setPosition", Entity, double.class, double.class, double.class).invoke(c, H.getLocation().getX(), H.getLocation().getY() - hh  - (GPM.getCManager().USE_ARMOR_STANDS ? 0 : 0.5) - (o == null ? 0.08 : 0), H.getLocation().getZ()); } catch(NullPointerException e) { }
				
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
		
		try {
			
			if(holos.containsKey(H)) {
				
				for(HoloRow sh : holos.get(H)) {
					
					Object r = PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance(new int[] { (Integer) NMSManager.invokeMethod("getId", sh.getBase()) });
					
					Object r2 = sh.getTop() != null ? PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance(new int[] { (Integer) NMSManager.invokeMethod("getId", sh.getTop()) }) : null;
					
					for(UUID u : H.getUUIDs()) {
						
						Player t = Bukkit.getPlayer(u);
						
						if(t != null && t.isOnline()) {
							
							if(r2 != null) NMSManager.sendPacket(t, r2);
							
							NMSManager.sendPacket(t, r);
							
						}
						
					}
					
				}
				
				if(re) holos.remove(H);
				
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
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
						
						if(!holo.getUUIDs().contains(t.getUniqueId())) {
							
							String text = GPM.getFormatUtil().formatSymbols(row);
							
							if(sh.needUpdate()) text = GPM.getFormatUtil().formatPlaceholders(text, t);
							
							text = GPM.getMManager().getGradientMessage(text);
							
							try { NMSManager.getMethod("setCustomNameVisible", Entity, boolean.class).invoke(sh.getBase(), sh.getTop() == null && !text.equalsIgnoreCase(Values.EMPTY_LINE_TEXT)); } catch(NullPointerException e) { }
							
							try {
								
								NMSManager.getMethod("setCustomName", Entity, IChatBaseComponent).invoke(sh.getBase(), IChatBaseComponent.cast(Object[].class.cast(CraftChatMessage.getMethod("fromString", String.class).invoke(null, text))[0]));
								
							} catch(NullPointerException e) {
								
								NMSManager.getMethod("setCustomName", Entity, String.class).invoke(sh.getBase(), text);
								
							}
							
							Object pa = NMSManager.isNewerOrVersion(14, 0) ? PacketPlayOutSpawnEntity.getConstructor(Entity).newInstance(sh.getBase()) : PacketPlayOutSpawnEntity.getConstructor(Entity, int.class).newInstance(sh.getBase(), 78);
							
							NMSManager.sendPacket(t, pa);
							
							Object pa2 = PacketPlayOutEntityMetadata.getConstructor(int.class, DataWatcher, boolean.class).newInstance((Integer) NMSManager.invokeMethod("getId", sh.getBase()), NMSManager.invokeMethod("getDataWatcher", sh.getBase()), true);
							
							NMSManager.sendPacket(t, pa2);
							
							if(sh.getTop() != null) {
								
								Object pa3 = NMSManager.isNewerOrVersion(14, 0) ? PacketPlayOutSpawnEntity.getConstructor(Entity).newInstance(sh.getTop()) : PacketPlayOutSpawnEntity.getConstructor(Entity, int.class).newInstance(sh.getTop(), 2);
								
								NMSManager.sendPacket(t, pa3);
								
								Object pa4 = PacketPlayOutEntityMetadata.getConstructor(int.class, DataWatcher, boolean.class).newInstance((Integer) NMSManager.invokeMethod("getId", sh.getTop()), NMSManager.invokeMethod("getDataWatcher", sh.getTop()), true);
								
								NMSManager.sendPacket(t, pa4);
								
								if(sh.getItem() != null) {
									
									NMSManager.getMethod("startRiding", Entity, Entity).invoke(sh.getItem(), sh.getBase());
									
									Object pa5 = PacketPlayOutMount.getConstructor(Entity).newInstance(sh.getBase());
									
									NMSManager.sendPacket(t, pa5);
									
								}
								
							}
							
						} else if(sh.needUpdate()) {
							
							if(sh.getTop() == null) {
								
								String text = GPM.getFormatUtil().formatSymbols(row);
								
								if(sh.needUpdate()) text = GPM.getFormatUtil().formatPlaceholders(text, t);
								
								text = GPM.getMManager().getGradientMessage(text);
								
								try { NMSManager.getMethod("setCustomNameVisible", Entity, boolean.class).invoke(sh.getBase(), sh.getTop() == null && !text.equalsIgnoreCase(Values.EMPTY_LINE_TEXT)); } catch(NullPointerException e) { }
								
								try {
									
									NMSManager.getMethod("setCustomName", Entity, IChatBaseComponent).invoke(sh.getBase(), IChatBaseComponent.cast(Object[].class.cast(CraftChatMessage.getMethod("fromString", String.class).invoke(null, text))[0]));
									
								} catch(NullPointerException e) {
									
									NMSManager.getMethod("setCustomName", Entity, String.class).invoke(sh.getBase(), text);
									
								}
								
								Object pa2 = PacketPlayOutEntityMetadata.getConstructor(int.class, DataWatcher, boolean.class).newInstance((Integer) NMSManager.invokeMethod("getId", sh.getBase()), NMSManager.invokeMethod("getDataWatcher", sh.getBase()), true);
								
								NMSManager.sendPacket(t, pa2);
								
							} else if(sh.getEntity() != null) {
								
								
								
							}
							
						}
						
					}
					
					if(rl.size() > 0) {
						
						Object r = PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance(new int[] { (Integer) NMSManager.invokeMethod("getId", sh.getBase()) });
						
						Object r2 = sh.getTop() != null ? PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance(new int[] { (Integer) NMSManager.invokeMethod("getId", sh.getTop()) }) : null;
						
						for(Player t : rl) {
							
							if(r2 != null) NMSManager.sendPacket(t, r2);
							
							NMSManager.sendPacket(t, r);
							
						}
						
					}
					
				}
				
				holo.clearUUIDs();
				
				for(Player t : o) holo.addUUID(t.getUniqueId());
				
			}
			
		} catch(ConcurrentModificationException e) { } catch(Exception e) { e.printStackTrace(); }
		
	}
	
	private static class HoloRow {
		
		private final int r;
		
		private final boolean u;
		
		private final Object c;
		
		private final Object i;
		
		private final Object e;
		
		public HoloRow(int R, boolean U, Object C, Object I, Object E) {
			r = R;
			u = U;
			c = C;
			i = I;
			e = E;
		}
		
		public int getRow() { return r; }
		
		public boolean needUpdate() { return u; }
		
		public Object getBase() { return c; }
		
		public Object getItem() { return i; }
		
		public Object getEntity() { return e; }
		
		public Object getTop() { return i != null ? i : e; }
		
	}
	
}