package org.terasology.fluid.system;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.math.Rect2i;
import org.terasology.math.Vector2i;
import org.terasology.registry.In;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.layers.ingame.inventory.BeforeInventoryCellRendered;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterSystem(RegisterMode.CLIENT)
public class FluidClientSystem extends BaseComponentSystem {
    @In
    private FluidRegistry fluidRegistry;

    @ReceiveEvent
    public void renderFluidInInventory(BeforeInventoryCellRendered event, EntityRef item, FluidContainerItemComponent component) {
        Canvas canvas = event.getCanvas();

        String fluidType = component.fluidType;
        if (fluidType != null) {
            FluidRenderer fluidRenderer = fluidRegistry.getFluidRenderer(fluidType);
            Vector2i size = canvas.size();

            fluidRenderer.renderFluid(canvas,
                    Rect2i.createFromMinAndSize(
                            Math.round(size.x * component.fluidMinPerc.x),
                            Math.round(size.y * component.fluidMinPerc.y),
                            Math.round(size.x * component.fluidSizePerc.x),
                            Math.round(size.x * component.fluidSizePerc.y)));
        } else {
            canvas.drawTexture(component.emptyTexture);
        }
    }
}
