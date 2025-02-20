package net.mehvahdjukaar.polytone.slotify;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record BlitModifier(ResourceLocation target, int index, int x, int y, int z, int width, int height,
                           float u0, float v0, float u1, float v1, Optional<ResourceLocation> newTexture,
                           List<RelativeSprite> extraSprites) {

    public static final Codec<BlitModifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(BlitModifier::target),
            Codec.INT.optionalFieldOf("index", -1).forGetter(BlitModifier::index),
            Codec.INT.optionalFieldOf("x_inc", 0).forGetter(BlitModifier::x),
            Codec.INT.optionalFieldOf("y_inc", 0).forGetter(BlitModifier::y),
            Codec.INT.optionalFieldOf("z_inc", 0).forGetter(BlitModifier::z),
            Codec.INT.optionalFieldOf("width_inc", 0).forGetter(BlitModifier::width),
            Codec.INT.optionalFieldOf("height_inc", 0).forGetter(BlitModifier::height),
            Codec.FLOAT.optionalFieldOf("u0", -1f).forGetter(BlitModifier::u0),
            Codec.FLOAT.optionalFieldOf("v0", -1f).forGetter(BlitModifier::v0),
            Codec.FLOAT.optionalFieldOf("u1", -1f).forGetter(BlitModifier::u1),
            Codec.FLOAT.optionalFieldOf("v1", -1f).forGetter(BlitModifier::v1),
            ResourceLocation.CODEC.optionalFieldOf("new_texture").forGetter(BlitModifier::newTexture),
            RelativeSprite.CODEC.listOf().optionalFieldOf("overlays", List.of()).forGetter(BlitModifier::extraSprites)
    ).apply(i, BlitModifier::new));


    public void blitModified(GuiGraphics gui, Function<ResourceLocation, RenderType> function, TextureAtlasSprite sprite, int x1, int x2, int y1, int y2, int blitOffset) {

        for (RelativeSprite s : extraSprites) {
            s.render(gui.pose(), x1, x2, y1, y2, blitOffset);
        }

        if (newTexture.isPresent()) {
            sprite = Minecraft.getInstance().getGuiSprites().getSprite(newTexture.get());
        }
        float minU = u1 == -1 ? sprite.getU0() : u1;
        float maxU = u0 == -1 ? sprite.getU1() : u0;
        float minV = v1 == -1 ? sprite.getV0() : v1;
        float maxV = v0 == -1 ? sprite.getV1() : v0;

        blitOffset += z;

        int oldw = x2 - x1;
        x1 += x;
        oldw += width;
        x2 = x1 + oldw;

        int oldh = y2 - y1;
        y1 += y;
        oldh += height;
        y2 = y1 + oldh;


        SimpleSprite.blit(gui.pose().last().pose(), sprite.atlasLocation(),
                (float) x1, (float) x2, (float) y1, (float) y2, (float) blitOffset, minU, maxU, minV, maxV);
    }


}
