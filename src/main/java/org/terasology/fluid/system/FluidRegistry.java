// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.system;

import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.world.block.Block;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * A generic fluid registry interface.
 */
public interface FluidRegistry {
    /**
     * Registers a fluid with a fluid renderer.
     *
     * @param fluidType     The type of fluid
     * @param displayName   The name used for the fluid in the UI
     * @param fluidTexture  The image to use when rendering the fluid
     * @param block         The corresponding liquid block, or null if this fluid can't be placed in the world.
     */
    void registerFluid(String fluidType, String displayName, BufferedImage fluidTexture, Block block);

    /**
     * Registers the fluid with no corresponding liquid block and a solid colour.
     *
     * @param fluidType   The type of fluid
     * @param displayName The name used for the fluid in the UI
     * @param color       The colour to use when rendering the fluid
     */
    void registerFluid(String fluidType, String displayName, Color color);

    /**
     * Accessor function which returns the list of fluid renderer associated with a given fluid type.
     *
     * @param fluidType The fluid type
     */
    BufferedImage getFluidTexture(String fluidType);

    /**
     * Accessor function which returns name to use in the UI for a given fluid type.
     *
     * @param fluidType The fluid type
     * @return The display name associated with the fluid type
     */
    String getDisplayName(String fluidType);

    /**
     * Accessor function which returns the liquid block associated with a given fluid type.
     *
     * @param fluidType The fluid type
     * @return The liquid block associated with the fluid type
     */
    Block getCorrespondingLiquid(String fluidType);

    /**
     * Accessor function which returns the fluid type associated with a given liquid block.
     *
     * @param liquid The type of liquid block
     * @return The fluid type associated with the liquid block
     */
    String getCorrespondingFluid(Block liquid);

    /**
     * Finds the prefab which should be used for properties of the given fluid, whether or not it has an associated liquid.
     */
    Prefab getPrefab(String fluidType);
}
