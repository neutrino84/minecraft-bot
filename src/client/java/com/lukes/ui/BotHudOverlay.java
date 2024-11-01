package com.lukes.ui;

import com.lukes.MBotEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
// import net.minecraft.client.render.VertexConsumerProvider;
// import net.minecraft.client.render.entity.EntityRenderDispatcher;
// import net.minecraft.util.math.RotationAxis;
// import org.joml.Matrix4f;
// import org.joml.Quaternionf;
import net.minecraft.text.Text;

public class BotHudOverlay {
    private static final int BOT_DISPLAY_SIZE = 32;
    private static final int MARGIN = 10;
    private final MinecraftClient client;
    private final BotUIManager botManager;

    public BotHudOverlay(MinecraftClient client, BotUIManager botManager) {
        this.client = client;
        this.botManager = botManager;
    }

    public void render(DrawContext context) {
        if (client.player == null) return;

        int y = MARGIN;
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Debug text to verify rendering
        context.drawTextWithShadow(
            client.textRenderer,
            Text.literal("Owned Bots: " + botManager.getOwnedBots().size()),
            MARGIN,
            MARGIN,
            0xFFFFFF
        );

        for (MBotEntity bot : botManager.getOwnedBots()) {
            if (y + BOT_DISPLAY_SIZE + MARGIN > screenHeight) break;
            
            // Draw box background
            context.fill(
                MARGIN,
                y + BOT_DISPLAY_SIZE + MARGIN,
                MARGIN + BOT_DISPLAY_SIZE,
                y + BOT_DISPLAY_SIZE * 2 + MARGIN,
                0x80000000
            );

            // Draw bot name
            if (bot.hasCustomName()) {
                context.drawTextWithShadow(
                    client.textRenderer,
                    bot.getCustomName(),
                    MARGIN + BOT_DISPLAY_SIZE + 5,
                    y + BOT_DISPLAY_SIZE + MARGIN + 2,
                    0xFFFFFF
                );
            }

            y += BOT_DISPLAY_SIZE + MARGIN;
        }
    }

    // private void renderBotAvatar(DrawContext context, int x, int y, MBotEntity bot) {
    //     // Draw background
    //     context.fill(x, y, x + BOT_DISPLAY_SIZE, y + BOT_DISPLAY_SIZE, 0x80000000);
        
    //     // Draw bot name
    //     if (bot.hasCustomName()) {
    //         context.drawTextWithShadow(client.textRenderer, 
    //             bot.getCustomName(), 
    //             x + BOT_DISPLAY_SIZE + 5, 
    //             y + 2, 
    //             0xFFFFFF);
    //     }
    // }
}