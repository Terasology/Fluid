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

import org.joml.Vector2f;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.logic.inventory.ItemDifferentiating;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.rendering.assets.texture.TextureRegionAsset;

/**
 * This component indicates that an entity is a container capable of holding fluids.
 */
public class FluidContainerItemComponent implements Component, ItemDifferentiating {

    /** The type of the fluid it can contain */
    @Replicate
    public String fluidType;

    /** The current volume of fluid in the container */
    public float volume;

    /** The maximum volume of fluid that the container can contain */
    public float maxVolume;

    /** The coordinate where the fluid 'filling' should start */
    public Vector2f fluidMinPerc;

    /** The coordinate where the fluid 'filling' should end */
    public Vector2f fluidSizePerc;

    /** The texture of the container when it is completely filled */
    public TextureRegionAsset<?> textureWithHole;

    /** The texture of the container when it is empty */
    public TextureRegionAsset<?> emptyTexture;

    /**
     * Checks whether the fluid container's attributes are the same as those of a given object.
     *
     * @param o The Object to compare the fluid container to
     * @return  Whether the fluid container's attributes are the same as that of the given object
     */
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
