package dev.geco.gholo.mcv.x.objects;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.*;

import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

public class HoloEntity extends Display.BlockDisplay {

    public HoloEntity(Location Location) {

        super(EntityType.BLOCK_DISPLAY, ((CraftWorld) Location.getWorld()).getHandle());

        persist = false;

        setWidth(0);
        setHeight(0);

        setViewRange(4);
    }

    public void move(MoverType MoverType, Vec3 Vec3) { }

    public boolean canChangeDimensions() { return false; }

    public boolean dismountsUnderwater() { return false; }

}