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

import org.terasology.assets.Asset;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;

import java.util.Optional;

@RegisterSystem(RegisterMode.CLIENT)
public class FluidClientSystem extends BaseComponentSystem {

    @In
    private AssetManager assetManager;

    @ReceiveEvent
    public void onFluidContentsActivated(OnActivatedComponent event, EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        setFluidContainerIcon(container, fluidContainerItem);
    }

    @ReceiveEvent
    public void onFluidContentsChanged(OnChangedComponent event, EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        setFluidContainerIcon(container, fluidContainerItem);
    }

    private void setFluidContainerIcon(EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        if (fluidContainerItem.emptyTexture != null
                && fluidContainerItem.fluidMinPerc != null
                && fluidContainerItem.fluidSizePerc != null
                && fluidContainerItem.textureWithHole != null) {
            String fluidType = fluidContainerItem.fluidType;
            if (fluidType != null) {
                if (fluidContainerItem.textureWithHole instanceof Asset) {
                    ItemComponent itemComp = container.getComponent(ItemComponent.class);

                    String iconUrn = FluidContainerAssetResolver.getFluidContainerUri(
                            fluidContainerItem.textureWithHole.getUrn().toString(),
                            fluidType,
                            fluidContainerItem.fluidMinPerc.x, fluidContainerItem.fluidMinPerc.y,
                            fluidContainerItem.fluidSizePerc.x, fluidContainerItem.fluidSizePerc.y);
                    Optional<Texture> icon = assetManager.getAsset(iconUrn, Texture.class);
                    itemComp.icon = icon.isPresent() ? icon.get() : fluidContainerItem.emptyTexture;
                    container.saveComponent(itemComp);
                }
            } else {
                ItemComponent itemComp = container.getComponent(ItemComponent.class);
                itemComp.icon = fluidContainerItem.emptyTexture;
                container.saveComponent(itemComp);
            }
        }
    }

}
