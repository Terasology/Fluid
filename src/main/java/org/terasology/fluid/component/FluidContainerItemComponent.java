package org.terasology.fluid.component;

import org.terasology.entitySystem.Component;
import org.terasology.logic.inventory.ItemDifferentiating;
import org.terasology.rendering.assets.texture.TextureRegion;

import javax.vecmath.Vector2f;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@ItemDifferentiating
public class FluidContainerItemComponent implements Component {
    public String fluidType;
    public float volume;
    public Vector2f fluidMinPerc;
    public Vector2f fluidSizePerc;
    public TextureRegion emptyTexture;

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
        if (fluidMinPerc != null ? !fluidMinPerc.equals(that.fluidMinPerc) : that.fluidMinPerc != null) {
            return false;
        }
        if (fluidSizePerc != null ? !fluidSizePerc.equals(that.fluidSizePerc) : that.fluidSizePerc != null) {
            return false;
        }
        if (fluidType != null ? !fluidType.equals(that.fluidType) : that.fluidType != null) {
            return false;
        }

        return true;
    }
}
