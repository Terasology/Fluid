// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.system;


import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockPart;
import org.terasology.engine.world.block.BlockUri;
import org.terasology.engine.world.block.loader.BlockFamilyDefinition;
import org.terasology.engine.world.block.loader.SectionDefinitionData;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;

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
