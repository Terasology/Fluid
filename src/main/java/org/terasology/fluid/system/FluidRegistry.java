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

import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.world.block.Block;

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
