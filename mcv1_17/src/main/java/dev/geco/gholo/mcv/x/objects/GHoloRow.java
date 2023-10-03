package dev.geco.gholo.mcv.x.objects;

import net.minecraft.world.entity.*;

public class GHoloRow {

    private final boolean update;

    private final Entity baseEntity;

    public GHoloRow(boolean Update, Entity BaseEntity) {

        update = Update;
        baseEntity = BaseEntity;
    }

    public boolean needUpdate() { return update; }

    public Entity getBase() { return baseEntity; }

}