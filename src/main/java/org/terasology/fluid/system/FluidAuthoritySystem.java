package org.terasology.fluid.system;

import org.terasology.asset.Asset;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.action.GiveItemAction;
import org.terasology.logic.inventory.action.RemoveItemAction;
import org.terasology.math.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.liquid.LiquidData;

import javax.vecmath.Vector3f;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class FluidAuthoritySystem extends BaseComponentSystem {
    @In
    private WorldProvider worldProvider;
    @In
    private FluidRegistry fluidRegistry;

    @ReceiveEvent(components = {ItemComponent.class})
    public void fillFluidContainerItem(ActivateEvent event, EntityRef item, FluidContainerItemComponent fluidContainer) {
        if (fluidContainer.fluidType == null) {
            Vector3f location = event.getInstigatorLocation();
            Vector3f direction = new Vector3f(event.getDirection());
            direction.normalize();
            for (int i = 0; i < 3; i++) {
                location.add(direction);
                LiquidData liquid = worldProvider.getLiquid(new Vector3i(location, 0.5f));
                if (liquid != null && liquid.getType() != null && liquid.getDepth() > 0) {
                    EntityRef owner = item.getOwner();
                    RemoveItemAction removeEvent = new RemoveItemAction(event.getInstigator(), item, false, 1);
                    owner.send(removeEvent);
                    if (removeEvent.isConsumed()) {
                        EntityRef removedItem = removeEvent.getRemovedItem();
                        FluidContainerItemComponent resultContainer = removedItem.getComponent(FluidContainerItemComponent.class);
                        String fluidType = fluidRegistry.getFluidType(liquid.getType());
                        resultContainer.fluidType = fluidType;
                        removedItem.saveComponent(resultContainer);

                        ItemComponent itemComp = removedItem.getComponent(ItemComponent.class);
                        itemComp.icon = Assets.getTexture("Fluid", "fluid(" + ((Asset) itemComp.icon).getURI().toSimpleString() + ")(" + fluidType + ")");
                        removedItem.saveComponent(itemComp);

                        GiveItemAction giveItem = new GiveItemAction(event.getInstigator(), removedItem);
                        owner.send(giveItem);
                        if (!giveItem.isConsumed()) {
                            removedItem.destroy();
                        }
                    }
                    return;
                }
            }
        }
    }
}
