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
package org.terasology.fluid.system;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.liquid.LiquidData;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class FluidAuthoritySystem extends BaseComponentSystem {
    @In
    private WorldProvider worldProvider;
    @In
    private FluidRegistry fluidRegistry;
    @In
    private InventoryManager inventoryManager;

    /**
     * Fill up the provided fluid container item with the current fluid interacted with in the game world.
     *
     * @param event             The event which has details about how this entity was activated.
     * @param item              The reference to the item being activated.
     * @param fluidContainer    The component used for storing fluid in a container.
     * @param itemComponent     A component included for filtering out non-matching events. Here, we only want entities
     *                          which are used as items.
     *
     */
    @ReceiveEvent
    public void fillFluidContainerItem(ActivateEvent event, EntityRef item, FluidContainerItemComponent fluidContainer,
                                       ItemComponent itemComponent) {
        if (fluidContainer.fluidType == null) {
            Vector3f location = event.getInstigatorLocation();
            Vector3f direction = new Vector3f(event.getDirection());
            direction.normalize();
            for (int i = 0; i < 3; i++) {
                location.add(direction);
                LiquidData liquid = worldProvider.getLiquid(new Vector3i(location, 0.5f));
                if (liquid != null && liquid.getType() != null && liquid.getDepth() > 0) {
                    EntityRef owner = item.getOwner();
                    final EntityRef removedItem = inventoryManager.removeItem(owner, event.getInstigator(), item, false, 1);
                    if (removedItem != null) {
                        String fluidType = fluidRegistry.getFluidType(liquid.getType());

                        // Set the contents of this fluid container and fill it up to max capacity.
                        FluidUtils.setFluidForContainerItem(removedItem, fluidType,
                                removedItem.getComponent(FluidContainerItemComponent.class).maxVolume);

                        if (!inventoryManager.giveItem(owner, event.getInstigator(), removedItem)) {
                            removedItem.destroy();
                        }
                    }
                    return;
                }
            }
        }
    }
}
