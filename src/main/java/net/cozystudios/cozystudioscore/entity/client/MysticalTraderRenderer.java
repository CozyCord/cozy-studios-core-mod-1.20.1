package net.cozystudios.cozystudioscore.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

public class MysticalTraderRenderer extends WanderingTraderEntityRenderer {
    private static final Identifier TEXTURE =
            new Identifier("cozystudioscore", "textures/entity/mystical_trader.png");

    public MysticalTraderRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(WanderingTraderEntity entity) {
        return TEXTURE;
    }
}