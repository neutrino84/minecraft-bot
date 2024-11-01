package com.lukes.ui;

import com.lukes.MBotEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BotUIManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("mbot");
    private final List<MBotEntity> ownedBots = new ArrayList<>();
    private final MinecraftClient client;

    public BotUIManager(MinecraftClient client) {
        this.client = client;
    }

    public void updateOwnedBots() {
        if (client.world == null || client.player == null) {
            LOGGER.debug("Cannot update bots: client world or player is null");
            return;
        }

        ownedBots.clear();
        
        LOGGER.debug("Starting bot update for player: " + client.player.getName().getString());

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof MBotEntity bot) {
                LOGGER.debug("Found bot with owner: " + bot.getOwnerUUID());
                if (bot.getOwnerUUID() != null && 
                    bot.getOwnerUUID().equals(client.player.getUuid())) {
                    ownedBots.add(bot);
                    LOGGER.debug("Found owned bot: " + bot.getCustomName().getString());
                }
            }
        }
        
        LOGGER.debug("Updated owned bots count: " + ownedBots.size());
    }

    public List<MBotEntity> getOwnedBots() {
        return ownedBots;
    }
}
