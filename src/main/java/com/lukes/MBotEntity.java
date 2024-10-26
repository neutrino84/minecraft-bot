package com.lukes;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.UUID;

public class MBotEntity extends PathAwareEntity {
    private UUID targetPlayerUUID;
    private String targetPlayerName;

    public MBotEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.setCanPickUpLoot(false);
        this.setPersistent();
    }
}
