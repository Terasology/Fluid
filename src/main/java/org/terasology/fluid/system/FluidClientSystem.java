// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.system;

import org.joml.Vector2i;
import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.assets.texture.TextureUtil;
import org.terasology.engine.utilities.Assets;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.module.inventory.ui.GetItemTooltip;
import org.terasology.module.inventory.ui.InventoryCellRendered;

import java.util.Optional;

/**
 * This client system handles client-side operations that occur when fluid components are changed.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class FluidClientSystem extends BaseComponentSystem {

    @In
    private FluidRegistry fluidRegistry;

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
            event.getTooltipLines().add(new TooltipLine("This holds " + (int) fluidContainerItem.volume + "/" +
                    (int) fluidContainerItem.maxVolume + "l of " + fluidRegistry.getDisplayName(fluidContainerItem.fluidType) + "."));
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

            canvas.drawTexture(Assets.get(backgroundTexture, Texture.class).get(), new Rectanglei(minX, minY, maxX, maxY));
            int fillingBarHeight = (int) (fillingPercentage * (maxY - minY - 1));
            int fillingBarLength = maxX - minX - 1;
            canvas.drawTexture(Assets.get(barTexture, Texture.class).get(), new Rectanglei(minX + 1,
                    maxY - fillingBarHeight - 1).setSize(fillingBarLength, fillingBarHeight));
        }
    }
}
