/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.fluid.component;

import org.terasology.entitySystem.Component;
import org.terasology.logic.inventory.ItemDifferentiating;
import org.terasology.math.geom.Vector2f;
import org.terasology.network.Replicate;
import org.terasology.rendering.assets.texture.TextureRegionAsset;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class FluidContainerItemComponent implements Component, ItemDifferentiating {
    @Replicate
    public String fluidType;
    public float volume;
    public float maxVolume;
    public Vector2f fluidMinPerc;
    public Vector2f fluidSizePerc;
    public TextureRegionAsset<?> textureWithHole;
    public TextureRegionAsset<?> emptyTexture;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FluidContainerItemComponent that = (FluidContainerItemComponent) o;

        if (Float.compare(that.volume, volume) != 0) {
            return false;
        }
        if (emptyTexture != null ? !emptyTexture.equals(that.emptyTexture) : that.emptyTexture != null) {
            return false;
        }
        if (fluidMinPerc != null ? !fluidMinPerc.equals(that.fluidMinPerc) : that.fluidMinPerc != null) {
            return false;
        }
        if (fluidSizePerc != null ? !fluidSizePerc.equals(that.fluidSizePerc) : that.fluidSizePerc != null) {
            return false;
        }
        if (fluidType != null ? !fluidType.equals(that.fluidType) : that.fluidType != null) {
            return false;
        }
        if (textureWithHole != null ? !textureWithHole.equals(that.textureWithHole) : that.textureWithHole != null) {
            return false;
        }

        return true;
    }
}
