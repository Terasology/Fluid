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

import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.block.Block;
import org.terasology.gestalt.naming.Name;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles registering and rendering of fluids.
 */
@RegisterSystem
@Share(FluidRegistry.class)
public class FluidRegistryImpl extends BaseComponentSystem implements FluidRegistry {
    private Map<Name, BufferedImage> fluidTextures = new HashMap<>();
    private Map<Name, String> displayNames = new HashMap<>();
    private Map<Name, Block> fluidLiquidMap = new HashMap<>();
    private Map<Block, Name> liquidFluidMap = new HashMap<>();

    /**
     * Registers a fluid with a fluid renderer.
     *
     * @param fluidTypeString The type of fluid
     * @param displayName     The name used for the fluid in the UI
     * @param fluidTexture    The image to use when rendering the fluid
     * @param block           The corresponding liquid block, or null if this fluid can't be placed in the world.
     */
    @Override
    public void registerFluid(String fluidTypeString, String displayName, BufferedImage fluidTexture, Block block) {
        Name fluidType = new Name(fluidTypeString);
        fluidTextures.put(fluidType, fluidTexture);
        displayNames.put(fluidType, displayName);
        if (block != null) {
            fluidLiquidMap.put(fluidType, block);
            liquidFluidMap.put(block, fluidType);
        }
    }

    /**
     * Registers the fluid with no corresponding liquid block and a solid colour.
     *
     * @param fluidType   The type of fluid
     * @param displayName The name used for the fluid in the UI
     * @param color       The colour to use when rendering the fluid
     */
    @Override
    public void registerFluid(String fluidType, String displayName, Color color) {
        BufferedImage texture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = texture.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0,0,16,16);
        registerFluid(fluidType, displayName, texture, null);
    }

    /**
     * Accessor function which returns the fluid image associated with a given fluid type.
     *
     * @param fluidType The fluid type
     * @return The fluid image associated with the fluid type
     */
    @Override
    public BufferedImage getFluidTexture(String fluidType) {
        return fluidTextures.get(new Name(fluidType));
    }

    /**
     * Accessor function which returns name to use in the UI for a given fluid type.
     *
     * @param fluidType The fluid type
     * @return The display name associated with the fluid type
     */
    @Override
    public String getDisplayName(String fluidType) {
        return displayNames.get(new Name(fluidType));
    }

    /**
     * Accessor function which returns the liquid block associated with a given fluid type.
     *
     * @param fluidType The fluid type
     * @return The liquid block associated with the fluid type
     */
    @Override
    public Block getCorrespondingLiquid(String fluidType) {
        return fluidLiquidMap.get(new Name(fluidType));
    }

    /**
     * Accessor function which returns the fluid type associated with a given liquid block.
     *
     * @param liquid The type of liquid block
     * @return The fluid type associated with the liquid block
     */
    @Override
    public String getCorrespondingFluid(Block liquid) {
        return liquidFluidMap.get(liquid).toString();
    }

    /**
     * Finds the prefab which should be used for properties of the given fluid, whether or not it has an associated liquid.
     */
    @Override
    public Prefab getPrefab(String fluidType) {
        if (fluidType == null) {
            return null;
        }
        Block liquidBlock = getCorrespondingLiquid(fluidType);
        if (liquidBlock != null) {
            return liquidBlock.getPrefab().orElse(null);
        } else {
            return CoreRegistry.get(PrefabManager.class).getPrefab(fluidType);
        }
    }
}
