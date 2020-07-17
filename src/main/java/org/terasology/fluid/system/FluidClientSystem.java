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
import org.terasology.assets.ResourceUrn;
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
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryCellRendered;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.utilities.Assets;

import java.util.Optional;

/**
 * This client system handles client-side operations that occur when fluid components are changed.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class FluidClientSystem extends BaseComponentSystem {

    @In
    private AssetManager assetManager;

    /**
     * Sets the tooltip of a fluid container.
     *
     * @param event                 Event that contains the tooltip lines.
     * @param container             Reference to the entity that acts a fluid container.
     * @param fluidContainerItem    The fluid container item component of the entity.
     */
    @ReceiveEvent
    public void setItemTooltip(GetItemTooltip event, EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        // Add tooltip with current fluid amounts.
        if (fluidContainerItem.fluidType != null) {
            event.getTooltipLines().add(new TooltipLine("This holds " + (int) (fluidContainerItem.volume) + "/" +
                    (int) (fluidContainerItem.maxVolume) + " ml of " + fluidContainerItem.fluidType + "."));
        } else {
            event.getTooltipLines().add(new TooltipLine("This holds no fluid."));
        }
    }

    /**
     * When the contents of this fluid container have been activated.
     *
     * @param event                 Event that indicates the activation.
     * @param container             Reference to the entity that acts a fluid container.
     * @param fluidContainerItem    The fluid container item component of the entity.
     */
    @ReceiveEvent
    public void onFluidContentsActivated(OnActivatedComponent event, EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        setFluidContainerIcon(container, fluidContainerItem);
    }

    /**
     * When the contents of this fluid container have been changed.
     *
     * @param event                 Event that indicates the activation.
     * @param container             Reference to the entity that acts a fluid container.
     * @param fluidContainerItem    The fluid container item component of the entity.
     */
    @ReceiveEvent
    public void onFluidContentsChanged(OnChangedComponent event, EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        setFluidContainerIcon(container, fluidContainerItem);
    }

    /**
     * Set the icon of this fluid container.
     *
     * @param container             Reference to the entity that acts a fluid container.
     * @param fluidContainerItem    The fluid container item component of the entity.
     */
    private void setFluidContainerIcon(EntityRef container, FluidContainerItemComponent fluidContainerItem) {
        // If this fluid container item has the both required textures (empty and filled), and percs.
        if (fluidContainerItem.emptyTexture != null
                && fluidContainerItem.fluidMinPerc != null
                && fluidContainerItem.fluidSizePerc != null
                && fluidContainerItem.textureWithHole != null) {
            String fluidType = fluidContainerItem.fluidType;

            // If there's already some fluid in this container.
            if (fluidType != null) {
                if (fluidContainerItem.textureWithHole instanceof Asset) {
                    ItemComponent itemComp = container.getComponent(ItemComponent.class);

                    // Set the icon of this fluid container to show that it's filled.
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

    /**
     * Used to draw the Filling bar over the fluid container item in the cell.
     *
     * @param event  An event sent after the inventory cell has been rendered.
     * @param entity The entity sending the request.
     * @param fluidContainer FluidContainerItemComponent of the item.
     */
    @ReceiveEvent
    public void drawFillingBarForFluidContainerItem(InventoryCellRendered event, EntityRef entity,
                                                    FluidContainerItemComponent fluidContainer) {
        Canvas canvas = event.getCanvas();

        Vector2i size = canvas.size();

        int minX = (int) (size.x * 0.8f);
        int maxX = (int) (size.x * 0.9f);

        int minY = (int) (size.y * 0.1f);
        int maxY = (int) (size.y * 0.9f);

        float fillingPercentage = fluidContainer.volume / fluidContainer.maxVolume;

        if (fillingPercentage > 0f && fillingPercentage < 1f) {
            ResourceUrn backgroundTexture = TextureUtil.getTextureUriForColor(Color.WHITE);
            ResourceUrn barTexture = TextureUtil.getTextureUriForColor(Color.BLUE);

            canvas.drawTexture(Assets.get(backgroundTexture, Texture.class).get(), Rect2i.createFromMinAndMax(minX,
                    minY, maxX, maxY));
            int fillingBarHeight = (int) (fillingPercentage * (maxY - minY - 1));
            int fillingBarLength = maxX - minX - 1;
            canvas.drawTexture(Assets.get(barTexture, Texture.class).get(), Rect2i.createFromMinAndSize(minX + 1,
                    maxY - fillingBarHeight - 1, fillingBarLength, fillingBarHeight ));
        }
    }
}
