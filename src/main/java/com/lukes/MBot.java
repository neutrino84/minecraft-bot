package com.lukes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;


import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBot implements ModInitializer {
	public static final String MOD_ID = "mbot";

	public static final EntityType<MBotEntity> HELPER_BOT = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MOD_ID, "helper_bot"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MBotEntity::new)
            .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
            .trackRangeBlocks(32)
            .build()
    );

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        LOGGER.info("Lukes Minecraft Bot Activated!");

		// Register the command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("spawnbot")
                .executes(context -> {
                    PlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        MBotEntity bot = HELPER_BOT.create(player.getWorld());
                        if (bot != null) {
                            bot.setPos(player.getX(), player.getY(), player.getZ());
                            bot.setTargetPlayer(player);
                            bot.setCustomName(Text.literal("Helper Bot"));
                            player.getWorld().spawnEntity(bot);
                            context.getSource().sendMessage(Text.literal("Helper Bot spawned and following you!"));
                        }
                    }
                    return 1;
                }));
        });
	}
}