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
package org.terasology.fluid.computer.module.inventory;

import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.computer.system.server.lang.os.condition.LatchCondition;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.event.FluidVolumeChangedInInventory;
import org.terasology.registry.Share;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(FluidManipulatorConditionsRegister.class)
public class FluidManipulatorServerSystem extends BaseComponentSystem implements FluidManipulatorConditionsRegister {
    private Map<EntityRef, LatchCondition> fluidChangeConditions = new HashMap<>();

    @Override
    public AbstractConditionCustomObject registerFluidInventoryChangeListener(EntityRef entity) {
        LatchCondition latchCondition = fluidChangeConditions.get(entity);
        if (latchCondition != null)
            return latchCondition;

        latchCondition = new LatchCondition();
        fluidChangeConditions.put(entity, latchCondition);

        return latchCondition;
    }

    @ReceiveEvent
    public void inventoryChange(FluidVolumeChangedInInventory event, EntityRef inventory) {
        processChangedInventory(inventory);
    }

    @ReceiveEvent
    public void inventoryRemoved(BeforeDeactivateComponent event, EntityRef inventory, FluidInventoryComponent inventoryComponent) {
        processChangedInventory(inventory);
    }

    @ReceiveEvent
    public void inventoryAdded(OnActivatedComponent event, EntityRef inventory, FluidInventoryComponent inventoryComponent) {
        processChangedInventory(inventory);
    }

    private void processChangedInventory(EntityRef inventory) {
        LatchCondition latchCondition = fluidChangeConditions.remove(inventory);
        if (latchCondition != null) {
            latchCondition.release(null);
        }
    }
}
