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


import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.registry.In;
import org.terasology.utilities.Assets;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockPart;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.loader.BlockFamilyDefinition;
import org.terasology.world.block.loader.SectionDefinitionData;

import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * This system is used to initialize fluid systems at launch time.
 */
@RegisterSystem
public class FluidCommonSystem extends BaseComponentSystem {
    @In
    private FluidRegistry fluidRegistry;

    @In
    private AssetManager assetManager;

    @In
    private BlockManager blockManager;

    /**
     * Initializes fluid resources and textures at launch time.
     */
    @Override
    public void preBegin() {
        for (ResourceUrn blockUrn : Assets.list(BlockFamilyDefinition.class)) {
            Optional<BlockFamilyDefinition> maybeDefinition = Assets.get(blockUrn, BlockFamilyDefinition.class);
            maybeDefinition.ifPresent(definition -> {
                SectionDefinitionData blockData = definition.getData().getBaseSection();
                if (blockData.isLiquid()) {
                    BufferedImage texture = blockData.getBlockTiles().get(BlockPart.FRONT).getImage();
                    Block block = blockManager.getBlock(new BlockUri(blockUrn));
                    fluidRegistry.registerFluid(blockUrn.toString(), block.getDisplayName(), texture, block);
                }
            });
        }
    }
}
