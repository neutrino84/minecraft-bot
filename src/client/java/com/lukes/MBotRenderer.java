package com.lukes;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class MBotRenderer extends MobEntityRenderer<MBotEntity, PlayerEntityModel<MBotEntity>> {
    private static final Identifier TEXTURE = new Identifier("mbot", "textures/entity/mbot.png");

    public MBotRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel<MBotEntity>(context.getPart(EntityModelLayers.PLAYER), false),  0.5f);
    }

    @Override
    public Identifier getTexture(MBotEntity entity) {
        return TEXTURE;
    }
}
