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
 * A generic fluid renderer interface.
 */
public interface FluidRenderer {
    /**
     * Accessor function that returns the renderer's texture.
     */
    TextureRegion getTexture();

    /**
     * Draws the texture in a given region on a given canvas.
     *
     * @param canvas the canvas to be drawn on
     * @param region the region on the canvas on which the texture is to be drawn
     */
    void renderFluid(Canvas canvas, Rect2i region);

    /**
     * Accessor function that returns the name of the fluid being rendered.
     */
    String getFluidName();
}
