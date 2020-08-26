// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.ui;

import org.terasology.fluid.system.FluidContainerAssetResolver;
import org.terasology.math.JomlUtil;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.nui.ScaleMode;
import org.terasology.utilities.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.system.FluidRegistry;
import org.joml.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;

/**
 * The UI widget for fluid containers.
 */
public class FluidContainerWidget extends CoreWidget {
    FluidRegistry fluidRegistry;

    @LayoutConfig
    private Binding<TextureRegion> image = new DefaultBinding<>(Assets.getTextureRegion("Fluid:FluidContainer").get());
    private InteractionListener listener = new BaseInteractionListener();

    private int minX;
    private int maxX;

    private int minY;
    private int maxY;

    private EntityRef entity;
    private int slotNo;

    /**
     * Default constructor.
     */
    public FluidContainerWidget() {
        fluidRegistry = CoreRegistry.get(FluidRegistry.class);
    }

    /**
     * Parametrized constructor with a specified ID.
     *
     * @param id The ID to assign to the fluid container
     */
    public FluidContainerWidget(String id) {
        super(id);
    }

    /**
     * Parametrized constructor with a specified image.
     *
     * @param image A specified image of the fluid container
     */
    public FluidContainerWidget(TextureRegion image) {
        this.image.set(image);
    }

    /**
     * Parametrized constructor with a specified ID and a specified image.
     * @param id    The ID to assign to the fuid container
     * @param image The image of the fluid container
     */
    public FluidContainerWidget(String id, TextureRegion image) {
        super(id);
        this.image.set(image);
    }

    /**
     * Defines how the fluid container widget is drawn.
     *
     * @param canvas The canvas on which the widget is drawn
     */
    @Override
    public void onDraw(Canvas canvas) {
        TextureRegion texture = getImage();
        if (texture != null) {
            FluidInventoryComponent fluidInventory = entity.getComponent(FluidInventoryComponent.class);
            FluidComponent fluid = fluidInventory.fluidSlots.get(slotNo).getComponent(FluidComponent.class);
            float maxVolume = fluidInventory.maximumVolumes.get(slotNo);
            float currentVolume = 0f;
            String fluidType = null;

            if (fluid != null) {
                currentVolume = fluid.volume;
                fluidType = fluid.fluidType;
                float result = fluid.volume / maxVolume;

                Vector2i size = canvas.size();
                int fluidMinY;
                int fluidMaxY;
                float yPerc = 1f * (minY + result * (maxY - minY)) / texture.getHeight();
                int y = Math.round(yPerc * size.y);
                if (minY < maxY) {
                    fluidMinY = minY;
                    fluidMaxY = y - minY;
                } else {
                    fluidMinY = y;
                    fluidMaxY = minY - y;
                }

                Texture fluidTexture = Assets.getTexture(FluidContainerAssetResolver.getFluidBaseUri(fluidType)).get();
                canvas.drawTextureRaw(fluidTexture, JomlUtil.rectangleiFromMinAndSize(minX, fluidMinY, maxX, fluidMaxY), ScaleMode.TILED);
            }

            canvas.drawTexture(texture, canvas.getRegion());

            setTooltipDelay(0);
            String fluidDisplay = fluidType == null ? "Fluid" : fluidRegistry.getDisplayName(fluidType);
            setTooltip(String.format(fluidDisplay + ": %.0f/%.0f", currentVolume, maxVolume));

        }

        canvas.addInteractionRegion(listener);
    }

    /**
     * Setter function to set the entity associated with the widget.
     *
     * @param entity The entity to associate with the widget
     */
    public void setEntity(EntityRef entity) {
        this.entity = entity;
    }

    /**
     * Setter function to set the slot number of the slot in which the container resides.
     *
     * @param slotNo The slot number
     */
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    /**
     * Gets the preferred size of the widget contents.
     *
     * @param canvas   The canvas on which the widget is to be drawn
     * @param sizeHint The size hint passed by the NUI system
     * @return         The preferred size of the widget
     */
    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        if (image.get() != null) {
            return image.get().size();
        }
        return new Vector2i();
    }

    /**
     * Accessor function which returns the image of the container widget.
     *
     * @return The image of the container
     */
    public TextureRegion getImage() {
        return image.get();
    }

    /**
     * Setter function to set the image of the container widget.
     *
     * @param image The image to set as the image of the widget
     */
    public void setImage(TextureRegion image) {
        this.image.set(image);
    }

    /**
     * Setter function to set the image of the widget to a given binding.
     *
     * @param binding The binding to set as the widget's image
     */
    public void bindTexture(Binding<TextureRegion> binding) {
        this.image = binding;
    }

    /**
     * Set the minimum Y co-ordinate where the widget can be drawn.
     *
     * @param minY The minimum Y co-ordinate where the widget can be drawn
     */
    public void setMinY(int minY) {
        this.minY = minY;
    }

    /**
     * Set the maximum Y co-ordinate where the widget can be drawn.
     *
     * @param maxY The maximum Y co-ordinate where the widget can be drawn
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    /**
     * Set the minimum X co-ordinate where the widget can be drawn.
     *
     * @param minX The minimum X co-ordinate where the widget can be drawn
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * Set the maximum X co-ordinate where the widget can be drawn.
     *
     * @param maxX The maximum X co-ordinate where the widget can be drawn
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
}
