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

import org.terasology.asset.Asset;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.logic.inventory.ItemComponent;

public final class FluidUtils {
    private FluidUtils() {
    }

    public static void setFluidForContainerItem(EntityRef container, String fluidType) {
        FluidContainerItemComponent resultContainer = container.getComponent(FluidContainerItemComponent.class);
        resultContainer.fluidType = fluidType;
        container.saveComponent(resultContainer);

        if (fluidType != null) {
            if (resultContainer.textureWithWhole instanceof Asset) {
                ItemComponent itemComp = container.getComponent(ItemComponent.class);
                itemComp.icon = Assets.getTexture("Fluid", "fluid(" + ((Asset) resultContainer.textureWithWhole).getURI().toSimpleString() + ")" +
                        "(" + fluidType + ")" +
                        "(" + resultContainer.fluidMinPerc.x + "," + resultContainer.fluidMinPerc.y + ")" +
                        "(" + resultContainer.fluidSizePerc.x + "," + resultContainer.fluidSizePerc.y + ")");
                container.saveComponent(itemComp);
            }
        } else {
            ItemComponent itemComp = container.getComponent(ItemComponent.class);
            itemComp.icon = resultContainer.emptyTexture;
            container.saveComponent(itemComp);
        }
    }
}
