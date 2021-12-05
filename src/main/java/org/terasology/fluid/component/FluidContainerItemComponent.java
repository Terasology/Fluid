// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.component;

import com.google.common.base.Objects;
import org.joml.Vector2f;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.rendering.assets.texture.TextureRegionAsset;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.module.inventory.components.ItemDifferentiating;

/**
 * This component indicates that an entity is a container capable of holding fluids.
 */
public class FluidContainerItemComponent implements Component<FluidContainerItemComponent>, ItemDifferentiating {

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
        return Float.compare(that.volume, volume) == 0
                && Float.compare(that.maxVolume, maxVolume) == 0
                && Objects.equal(fluidType, that.fluidType)
                && Objects.equal(fluidMinPerc, that.fluidMinPerc)
                && Objects.equal(fluidSizePerc, that.fluidSizePerc)
                && Objects.equal(textureWithHole, that.textureWithHole)
                && Objects.equal(emptyTexture, that.emptyTexture);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fluidType, volume, maxVolume, fluidMinPerc, fluidSizePerc, textureWithHole, emptyTexture);
    }

    @Override
    public void copyFrom(FluidContainerItemComponent other) {
        this.fluidType = other.fluidType;
        this.volume = other.volume;
        this.maxVolume = other.maxVolume;
        this.fluidMinPerc = new Vector2f(other.fluidMinPerc);
        this.fluidSizePerc = new Vector2f(other.fluidSizePerc);
        this.textureWithHole = other.textureWithHole;
        this.emptyTexture = other.emptyTexture;
    }
}
