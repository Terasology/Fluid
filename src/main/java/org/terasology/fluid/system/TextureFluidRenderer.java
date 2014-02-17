package org.terasology.fluid.system;

import org.terasology.math.Rect2i;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.Canvas;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class TextureFluidRenderer implements FluidRenderer {
    private Texture texture;

    public TextureFluidRenderer(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void renderFluid(Canvas canvas, Rect2i region) {
        canvas.drawTexture(texture, region);
    }
}
