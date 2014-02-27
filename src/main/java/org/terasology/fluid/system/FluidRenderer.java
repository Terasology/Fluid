package org.terasology.fluid.system;

import org.terasology.math.Rect2i;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.Canvas;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public interface FluidRenderer {
    Texture getTexture();

    void renderFluid(Canvas canvas, Rect2i region);

    String getFluidName();
}
