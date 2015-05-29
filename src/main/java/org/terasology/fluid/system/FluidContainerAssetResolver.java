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

import com.google.common.collect.ImmutableSet;
import org.terasology.assets.AssetDataProducer;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.assets.module.annotations.RegisterAssetDataProducer;
import org.terasology.math.Vector2i;
import org.terasology.naming.Name;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureData;
import org.terasology.rendering.assets.texture.TextureRegionAsset;
import org.terasology.rendering.assets.texture.TextureUtil;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterAssetDataProducer
public class FluidContainerAssetResolver implements AssetDataProducer<TextureData> {
    private static final Name FLUID_MODULE = new Name("fluid");

    private final AssetManager assetManager;

    public FluidContainerAssetResolver(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public static String getFluidContainerUri(String textureUri, String fluidType, float minPercX, float minPercY,
                                              float sizePercX, float sizePercY) {
        StringBuilder sb = new StringBuilder();

        sb.append("Fluid:Fluid(");
        sb.append(textureUri);
        sb.append(",").append(fluidType);
        sb.append(",").append(minPercX).append(",").append(minPercY);
        sb.append(",").append(sizePercX).append(",").append(sizePercY);
        sb.append(")");

        return sb.toString();
    }


    @Override
    public Set<ResourceUrn> getAvailableAssetUrns() {
        return Collections.emptySet();
    }

    @Override
    public Set<Name> getModulesProviding(Name resourceName) {
        if (!resourceName.toLowerCase().startsWith("fluid(")) {
            return Collections.emptySet();
        }
        return ImmutableSet.of(FLUID_MODULE);
    }

    @Override
    public ResourceUrn redirect(ResourceUrn urn) {
        return urn;
    }

    @Override
    public Optional<TextureData> getAssetData(ResourceUrn urn) throws IOException {
        final String assetName = urn.getResourceName().toString().toLowerCase();
        if (!FLUID_MODULE.equals(urn.getModuleName())
                || !assetName.startsWith("fluid(")) {
            return Optional.empty();
        }
        String[] split = assetName.split("\\(");

        String[] parameters = split[1].substring(0, split[1].length() - 1).split(",");

        String textureWithHole = parameters[0];
        String fluidType = parameters[1];

        FluidRenderer fluidRenderer = CoreRegistry.get(FluidRegistry.class).getFluidRenderer(fluidType);
        BufferedImage fluidTexture = TextureUtil.convertToImage(fluidRenderer.getTexture());

        Optional<TextureRegionAsset> textureWithHoleRegion = assetManager.getAsset(textureWithHole,
                TextureRegionAsset.class);
        BufferedImage containerTexture = TextureUtil.convertToImage(textureWithHoleRegion.get());
        int width = containerTexture.getWidth();
        int height = containerTexture.getHeight();

        int fluidWidth = fluidTexture.getWidth();
        int fluidHeight = fluidTexture.getHeight();

        Vector2i min = new Vector2i(
                Math.round(Float.parseFloat(parameters[2]) * width),
                Math.round(Float.parseFloat(parameters[3]) * height));
        Vector2i size = new Vector2i(
                Math.round(Float.parseFloat(parameters[4]) * width),
                Math.round(Float.parseFloat(parameters[5]) * height));

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        try {
            // Draw fluid texture tiled in the designated space
            for (int x = min.x; x < min.x + size.x; x += fluidWidth) {
                for (int y = min.y; y < min.y + size.y; y += fluidHeight) {
                    int fluidTileWidth = Math.min(fluidWidth, size.x + min.x - x);
                    int fluidTileHeight = Math.min(fluidHeight, size.y + min.y - y);
                    graphics.drawImage(fluidTexture, x, y, x + fluidTileWidth, y + fluidTileHeight, 0, 0, fluidTileWidth, fluidTileHeight, null);
                }
            }
            // Draw the container texture on top of the fluid
            graphics.drawImage(containerTexture, 0, 0, null);
        } finally {
            graphics.dispose();
        }

        final ByteBuffer resultBuffer = TextureUtil.convertToByteBuffer(result);

        TextureData data = new TextureData(width, height, new ByteBuffer[]{resultBuffer}, Texture.WrapMode.REPEAT,
                Texture.FilterMode.NEAREST);
        return Optional.of(data);

    }
}
