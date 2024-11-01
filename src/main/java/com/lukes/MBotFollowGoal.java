package com.lukes;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class MBotFollowGoal extends Goal {
    private final MBotEntity mbot;
    private PlayerEntity targetPlayer;
    private final double speed;
    private final EntityNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;

    public MBotFollowGoal(MBotEntity mbot, double speed) {
        this.mbot = mbot;
        this.speed = speed;
        this.navigation = mbot.getNavigation();
        this.stopDistance = 5.0F;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.mbot.getTargetPlayerUUID() == null) {
            return false;
        }

        PlayerEntity player = this.mbot.getWorld().getPlayerByUuid(this.mbot.getTargetPlayerUUID());
        if (player == null) {
            return false;
        }

        this.targetPlayer = player;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart() && !this.navigation.isIdle() && 
               this.mbot.squaredDistanceTo(this.targetPlayer) > (double)(this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        if (this.targetPlayer != null && !this.mbot.isLeashed()) {
            this.mbot.getLookControl().lookAt(this.targetPlayer, 10.0F, (float)this.mbot.getMaxHeadRotation());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (this.mbot.squaredDistanceTo(this.targetPlayer) > (double)(this.stopDistance * this.stopDistance)) {
                    this.navigation.startMovingTo(this.targetPlayer, this.speed);
                }
            }
        }
    }
}
