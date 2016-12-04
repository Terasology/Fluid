/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.fluid.system;

import org.terasology.math.geom.Rect2i;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;

/**
 * Handles how fluids are rendered on the screen.
 */
public class TextureFluidRenderer implements FluidRenderer {
    private TextureRegion texture;
    private String fluidName;

    /**
     * Parametrized constructor.
     *
     * @param texture   the texture to be rendered
     * @param fluidName the name of the fluid to be rendered
     */
    public TextureFluidRenderer(TextureRegion texture, String fluidName) {
        this.texture = texture;
        this.fluidName = fluidName;
    }

    /**
     * Accessor function which returns the texture being rendered.
     *
     * @return the texture being rendered
     */
    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    /**
     * Draws the texture on the given canvas, in the given region.
     *
     * @param canvas the canvas to be drawn on
     * @param region the region on the canvas on which the texture is to be drawn
     */
    @Override
    public void renderFluid(Canvas canvas, Rect2i region) {
        canvas.drawTexture(texture, region);
    }

    /**
     * Accessor function which returns the name of the fluid being rendered.
     *
     * @return the name of the fluid being rendered
     */
    @Override
    public String getFluidName() {
        return fluidName;
    }
}
