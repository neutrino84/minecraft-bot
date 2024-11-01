package com.lukes;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class MBotEntity extends PathAwareEntity {
    private UUID ownerUUID;
    private UUID targetPlayerUUID;

    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(MBotEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Optional<UUID>> TARGET_UUID = DataTracker.registerData(MBotEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

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
        if (this.ownerUUID != null) {
            nbt.putUuid("OwnerUUID", this.ownerUUID);
        }
        if (this.targetPlayerUUID != null) {
            nbt.putUuid("TargetPlayerUUID", this.targetPlayerUUID);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OwnerUUID")) {
            this.ownerUUID = nbt.getUuid("OwnerUUID");
        }
        if (nbt.contains("TargetPlayerUUID")) {
            this.targetPlayerUUID = nbt.getUuid("TargetPlayerUUID");
        }
    }

    public void setOwner(PlayerEntity player) {
        this.ownerUUID = player.getUuid();
        this.dataTracker.set(OWNER_UUID, Optional.of(player.getUuid()));
    }

    public UUID getOwnerUUID() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setTargetPlayer(PlayerEntity player) {
        this.targetPlayerUUID = player.getUuid();
        this.dataTracker.set(TARGET_UUID, Optional.of(player.getUuid()));
    }

    public UUID getTargetPlayerUUID() {
        return this.dataTracker.get(TARGET_UUID).orElse(null);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
        this.dataTracker.startTracking(TARGET_UUID, Optional.empty());
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isFireImmune() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }
}
