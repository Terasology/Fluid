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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.fluid.component.FluidInventoryComponent;

public final class FluidUtils {
    private FluidUtils() {
    }

    public static void setFluidForContainerItem(EntityRef container, String fluidType) {
        FluidContainerItemComponent resultContainer = container.getComponent(FluidContainerItemComponent.class);
        resultContainer.fluidType = fluidType;
        container.saveComponent(resultContainer);
    }

    public static String getFluidAt(EntityRef entity, int slot) {
        FluidInventoryComponent fluidInventoryComponent = entity.getComponent(FluidInventoryComponent.class);
        if (fluidInventoryComponent != null) {
            EntityRef fluidEntity = fluidInventoryComponent.fluidSlots.get(slot);
            FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
            if (fluid != null) {
                return fluid.fluidType;
            }
        }

        return null;
    }

    public static float getFluidAmount(EntityRef entity, int slot) {
        FluidInventoryComponent fluidInventoryComponent = entity.getComponent(FluidInventoryComponent.class);
        if (fluidInventoryComponent != null) {
            EntityRef fluidEntity = fluidInventoryComponent.fluidSlots.get(slot);
            FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
            if (fluid != null) {
                return fluid.volume;
            }
        }

        return 0;
    }
}
