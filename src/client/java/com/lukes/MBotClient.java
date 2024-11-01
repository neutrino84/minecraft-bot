package com.lukes;

import com.lukes.ui.BotHudOverlay;
import com.lukes.ui.BotUIManager;
import com.lukes.networking.BotSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBotClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("mbot");
	private BotUIManager botManager;
    private BotHudOverlay hudOverlay;

	@Override
	public void onInitializeClient() {
		// Register Helper Bot Renderer
		EntityRendererRegistry.register(MBot.HELPER_BOT, MBotRenderer::new);

		// Initialize UI components
		MinecraftClient client = MinecraftClient.getInstance();
		botManager = new BotUIManager(client);
		hudOverlay = new BotHudOverlay(client, botManager);

        // Register HUD renderer
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            try {
                botManager.updateOwnedBots();
                hudOverlay.render(drawContext);
            } catch (Exception e) {
                LOGGER.error("Error rendering bot HUD: ", e);
            }
        });

		// Register network receiver
        ClientPlayNetworking.registerGlobalReceiver(BotSyncPacket.BOT_SYNC, (client1, handler, buf, responseSender) -> {
            long timestamp = buf.readLong(); // Read the timestamp
            client1.execute(() -> {
                LOGGER.debug("Received bot sync packet at " + timestamp);
                if (client1.player != null) {
                    botManager.updateOwnedBots();
                    LOGGER.debug("Updated owned bots count: " + botManager.getOwnedBots().size());
                }
            });
        });

        LOGGER.info("MBot Client initialized successfully!");
	}
}