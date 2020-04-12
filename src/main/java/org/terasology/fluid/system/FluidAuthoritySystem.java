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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
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
import org.terasology.world.block.Block;

import java.math.RoundingMode;
import java.util.Optional;

/**
 * This authority system handles how fluid items interact with the game world and how they are filled in containers.
 * <p>
 * It is currently unused pending a proper liquids module being made.
 * Removed as a part of PR MovingBlocks/Terasology#3495
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class FluidAuthoritySystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(FluidAuthoritySystem.class);

    @In
    private WorldProvider worldProvider;
    @In
    private FluidRegistry fluidRegistry;
    @In
    private InventoryManager inventoryManager;

    /**
     * Search for a reachable liquid block in the given direction.
     *
     * A liquid block is reachable iff it is within the specified distance from the starting location and
     * there is no penetrable block preventing direct access.
     * If the returned option is non-empty, the block is guaranteed to be a liquid block.
     *
     * @param start		the starting location
     * @param direction	the direction to search for a liquid block
     * @param distance	the reachable distance in number of blocks
     *
     * @return option of the liquid block found in reach, empty if none was found
     */
    private Optional<Block> getLiquidInReach(final Vector3f start, final Vector3f direction, int distance) {
        Vector3f location = new Vector3f(start);
        Vector3f normalizedDirection = new Vector3f(direction).normalize();
        for (int i = 0; i < distance; i++) {
            location.add(normalizedDirection);
            Block block = worldProvider.getBlock(new Vector3i(location, RoundingMode.HALF_DOWN));
            if (block.isLiquid()) {
                return Optional.of(block);
            }
            if (!block.isPenetrable()) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Fill up the provided fluid container item with the current fluid interacted with in the game world.
     *
     * @param event          The event which has details about how this entity was activated.
     * @param item           The reference to the item being activated.
     * @param fluidContainer The component used for storing fluid in a container.
     * @param itemComponent  A component included for filtering out non-matching events. Here, we only want entities
     *                       which are used as items.
     */
    @ReceiveEvent(priority= EventPriority.PRIORITY_HIGH)
    public void fillFluidContainerItem(ActivateEvent event, EntityRef item, FluidContainerItemComponent fluidContainer,
                                       ItemComponent itemComponent) {
        if (fluidContainer.fluidType == null || fluidContainer.volume < fluidContainer.maxVolume) {
            getLiquidInReach(event.getInstigatorLocation(), event.getDirection(), 3).ifPresent(block -> {
                EntityRef owner = item.getOwner();
                final EntityRef removedItem = inventoryManager.removeItem(owner, event.getInstigator(), item, false, 1);
                //TODO: replace with better fluid handling, maybe by new CoreFluids module
                FluidContainerItemComponent removedFluidContainer= removedItem.getComponent(FluidContainerItemComponent.class);
                float volume= Math.min((removedFluidContainer.volume + removedFluidContainer.fillingAmount), removedFluidContainer.maxVolume);
                if (removedItem != null && block.isWater()) {
                    // Set the contents of this fluid container and fill it up to (current volume + filling amount) or maxVolume whichever is less.
                    FluidUtils.setFluidForContainerItem(removedItem, "Fluid:Water", volume);
                    if (!inventoryManager.giveItem(owner, event.getInstigator(), removedItem)) {
                        removedItem.destroy();
                    }
                }
                event.consume();
            });
        }
    }
}
