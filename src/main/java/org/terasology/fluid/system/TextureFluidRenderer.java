package org.terasology.fluid.system;

import org.terasology.math.Rect2i;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class TextureFluidRenderer implements FluidRenderer {
    private TextureRegion texture;
    private String fluidName;

    public TextureFluidRenderer(TextureRegion texture, String fluidName) {
        this.texture = texture;
        this.fluidName = fluidName;
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    @Override
    public void renderFluid(Canvas canvas, Rect2i region) {
        canvas.drawTexture(texture, region);
    }

    @Override
    public String getFluidName() {
        return fluidName;
    }
}
