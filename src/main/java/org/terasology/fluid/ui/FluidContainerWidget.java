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
package org.terasology.fluid.ui;

import org.terasology.utilities.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.system.FluidRegistry;
import org.terasology.fluid.system.FluidRenderer;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.BaseInteractionListener;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.InteractionListener;
import org.terasology.rendering.nui.LayoutConfig;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;

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
     * @param id the ID to assign to the fluid container
     */
    public FluidContainerWidget(String id) {
        super(id);
    }

    /**
     * Parametrized constructor with a specified image.
     *
     * @param image a specified image of the fluid container
     */
    public FluidContainerWidget(TextureRegion image) {
        this.image.set(image);
    }

    /**
     * Parametrized constructor with a specified ID and a specified image.
     * @param id    the ID to assign to the fuid container
     * @param image the image of the fluid container
     */
    public FluidContainerWidget(String id, TextureRegion image) {
        super(id);
        this.image.set(image);
    }

    /**
     * Defines how the fluid container widget is drawn.
     *
     * @param canvas the canvas on which the widget is drawn
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
            FluidRenderer fluidRenderer = null;

            if (fluid != null) {
                currentVolume = fluid.volume;
                fluidType = fluid.fluidType;
                float result = fluid.volume / maxVolume;

                fluidRenderer = fluidRegistry.getFluidRenderer(fluid.fluidType);

                Vector2i size = canvas.size();
                if (minY < maxY) {
                    float yPerc = 1f * (minY + result * (maxY - minY)) / texture.getHeight();
                    fluidRenderer.renderFluid(canvas, Rect2i.createFromMinAndSize(minX, minY, maxX, Math.round(yPerc * size.y) - minY));
                } else {
                    float yPerc = 1f * (minY - result * (minY - maxY)) / texture.getHeight();
                    int y = Math.round(yPerc * size.y);
                    fluidRenderer.renderFluid(canvas, Rect2i.createFromMinAndSize(minX, y, maxX, minY - y));
                }
            }

            canvas.drawTexture(texture, canvas.getRegion());

            setTooltipDelay(0);
            String fluidDisplay = fluidType == null ? "Fluid" : fluidRenderer.getFluidName();
            setTooltip(String.format(fluidDisplay + ": %.0f/%.0f", currentVolume, maxVolume));

        }

        canvas.addInteractionRegion(listener);
    }

    /**
     * Setter function to set the entity associated with the widget.
     *
     * @param entity the entity to associate with the widget
     */
    public void setEntity(EntityRef entity) {
        this.entity = entity;
    }

    /**
     * Setter function to set the slot number of the slot in which the container resides.
     *
     * @param slotNo the slot number
     */
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    /**
     * Gets the preferred size of the widget contents.
     *
     * @param canvas   the canvas on which the widget is to be drawn
     * @param sizeHint the size hint passed by the NUI system
     * @return         the preferred size of the widget
     */
    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        if (image.get() != null) {
            return image.get().size();
        }
        return Vector2i.zero();
    }

    /**
     * Accessor function which returns the image of the container widget.
     *
     * @return the image of the container
     */
    public TextureRegion getImage() {
        return image.get();
    }

    /**
     * Setter function to set the image of the container widget.
     * @param image the image to set as the image of the widget
     */
    public void setImage(TextureRegion image) {
        this.image.set(image);
    }

    /**
     * Setter function to set the image of the widget to a given binding.
     *
     * @param binding the binding to set as the widget's image
     */
    public void bindTexture(Binding<TextureRegion> binding) {
        this.image = binding;
    }

    /**
     * Set the minimum Y co-ordinate where the widget can be drawn.
     *
     * @param minY the minimum Y co-ordinate where the widget can be drawn
     */
    public void setMinY(int minY) {
        this.minY = minY;
    }

    /**
     * Set the maximum Y co-ordinate where the widget can be drawn.
     *
     * @param maxY the maximum Y co-ordinate where the widget can be drawn
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    /**
     * Set the minimum X co-ordinate where the widget can be drawn.
     *
     * @param minX the minimum X co-ordinate where the widget can be drawn
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * Set the maximum X co-ordinate where the widget can be drawn.
     *
     * @param maxX the maximum X co-ordinate where the widget can be drawn
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
}
