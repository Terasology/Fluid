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

import org.terasology.asset.AssetFactory;
import org.terasology.asset.AssetResolver;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.asset.Assets;
import org.terasology.math.Vector2i;
import org.terasology.naming.Name;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureData;
import org.terasology.rendering.assets.texture.TextureUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class FluidContainerAssetResolver implements AssetResolver<Texture, TextureData> {
    private static final Name FLUID_MODULE = new Name("fluid");

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
    public AssetUri resolve(Name partialUri) {

        String[] parts = partialUri.toString().split("\\(", 2);
        if (parts.length > 1) {
            AssetUri uri = Assets.resolveAssetUri(AssetType.TEXTURE, parts[0]);
            if (uri != null) {
                return new AssetUri(AssetType.TEXTURE, uri.getModuleName(), partialUri);
            }
        }
        return null;
    }

    @Override
    public Texture resolve(AssetUri uri, AssetFactory<TextureData, Texture> factory) {
        final String assetName = uri.getAssetName().toString().toLowerCase();
        if (!FLUID_MODULE.equals(uri.getModuleName())
                || !assetName.startsWith("fluid(")) {
            return null;
        }
        String[] split = assetName.split("\\(");

        String[] parameters = split[1].substring(0, split[1].length() - 1).split(",");

        String textureWithHole = parameters[0];
        String fluidType = parameters[1];

        BufferedImage fluidTexture = TextureUtil.convertToImage(CoreRegistry.get(FluidRegistry.class).getFluidRenderer(fluidType).getTexture());

        BufferedImage containerTexture = TextureUtil.convertToImage(Assets.getTextureRegion(textureWithHole));
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

        return factory.buildAsset(uri, new TextureData(width, height, new ByteBuffer[]{resultBuffer}, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));
    }
}
