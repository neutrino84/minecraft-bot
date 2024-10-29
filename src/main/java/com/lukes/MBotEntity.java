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

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MBotFollowGoal(this, 1.0D));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.targetPlayerUUID != null) {
            nbt.putUuid("TargetPlayerUUID", this.targetPlayerUUID);
        }
        if (this.targetPlayerName != null) {
            nbt.putString("TargetPlayerName", this.targetPlayerName);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TargetPlayerUUID")) {
            this.targetPlayerUUID = nbt.getUuid("TargetPlayerUUID");
        }
        if (nbt.contains("TargetPlayerName")) {
            this.targetPlayerName = nbt.getString("TargetPlayerName");
        }
    }

    public void setTargetPlayer(PlayerEntity player) {
        this.targetPlayerUUID = player.getUuid();
        this.targetPlayerName = player.getName().getString();
    }

    public UUID getTargetPlayerUUID() {
        return this.targetPlayerUUID;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }
}
