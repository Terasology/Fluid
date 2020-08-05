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
package org.terasology.fluid.system;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.AssetDataProducer;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.assets.module.annotations.RegisterAssetDataProducer;
import org.terasology.math.geom.Vector2i;
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
 * Generates TextureData for the asset system by combining fluid textures with fluid container item textures, or just by tiling fluid textures to a specified size.
 */
@RegisterAssetDataProducer
public class FluidContainerAssetResolver implements AssetDataProducer<TextureData> {
    private static final Logger logger = LoggerFactory.getLogger(FluidContainerAssetResolver.class);

    private static final Name FLUID_MODULE = new Name("fluid");

    private final AssetManager assetManager;

    /**
     * Parametrized constructor.
     *
     * @param assetManager The asset manager to be used for asset resolution
     */
    public FluidContainerAssetResolver(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Generates a URI for a texture with specific fluid type and co-ordinate bounds.
     *
     * @param textureUri The name of the texture whose URI is to be generated
     * @param fluidType  The type of the fluid whose texture's URI is to be generated
     * @param minPercX   The X co-ordinate where the fluid 'filling' should start
     * @param minPercY   The Y co-ordinate where the fluid 'filling' should start
     * @param sizePercX  The X co-ordinate where the fluid 'filling' should end
     * @param sizePercY  The Y co-ordinate where the fluid 'filling' should end
     * @return           The URI to the required texture
     */
    public static String getFluidContainerUri(String textureUri, String fluidType, float minPercX, float minPercY,
                                              float sizePercX, float sizePercY) {
        StringBuilder sb = new StringBuilder();

        sb.append("Fluid:FluidItem(");
        sb.append(textureUri);
        sb.append(",").append(fluidType);
        sb.append(",").append(minPercX).append(",").append(minPercY);
        sb.append(",").append(sizePercX).append(",").append(sizePercY);
        sb.append(")");

        return sb.toString();
    }

    /**
     * Generates a URI for a texture with specific fluid type and the default size.
     *
     * @param fluidType  The type of the fluid whose texture's URI is to be generated
     * @return           The URI to the required texture
     */
    public static String getFluidBaseUri(String fluidType) {
        StringBuilder sb = new StringBuilder();

        sb.append("Fluid:FluidBase(");
        sb.append(fluidType);
        sb.append(")");

        return sb.toString();
    }

    /**
     * Get a set of all available asset URNs
     *
     * @return A set of available asset URNs
     */
    @Override
    public Set<ResourceUrn> getAvailableAssetUrns() {
        return Collections.emptySet();
    }

    /**
     * Returns the names of the modules for which this system can produce asset data with the given resource name.
     *
     * @param resourceName The name of the resource
     * @return             A set of the names of modules for which this system can produce asset data with the given
     *                     resource name
     */
    @Override
    public Set<Name> getModulesProviding(Name resourceName) {
        if (!resourceName.toLowerCase().startsWith("fluid(")) {
            return Collections.emptySet();
        }
        return ImmutableSet.of(FLUID_MODULE);
    }

    /**
     * Redirects a given asset URN.
     *
     * @param urn The URN to be redirected
     * @return    The redirected URN
     */
    @Override
    public ResourceUrn redirect(ResourceUrn urn) {
        return urn;
    }

    /**
     * Fetches asset data from a given URN.
     *
     * @param urn          The URN from where data is to be fetched
     * @return             The asset data that has been fetched
     * @throws IOException If an input or output error occured
     */
    @Override
    public Optional<TextureData> getAssetData(ResourceUrn urn) throws IOException {
        final String assetName = urn.getResourceName().toString().toLowerCase();
        if (!FLUID_MODULE.equals(urn.getModuleName())
                || !(assetName.startsWith("fluiditem(") || assetName.startsWith("fluidbase("))) {
            return Optional.empty();
        }

        FluidRegistry fluidRegistry = CoreRegistry.get(FluidRegistry.class);
        if (fluidRegistry == null) {
            // Sometimes in multiplayer it loads things involving assets from the server before the systems are initialized.
            return Optional.empty();
        }

        boolean isItem = assetName.startsWith("fluiditem");
        String[] split = assetName.split("\\(");

        // Get the parameters from the URN.
        String[] parameters = null;

        // If the URN's fragment name is non-empty, then that means this item is using a texture atlas.
        // Thus, the necessary information is contained in the resource name (specifically split[1]) and fragment name.
        if (!urn.getFragmentName().toString().equals("")) {
            // Break up the string using commas.
            parameters = urn.getFragmentName().toString().split(",");

            // As this is a texture atlas, combine the location of the atlas (split[1]) with the name of the element in
            // the atlas (parameters[0]), and store it back into parameters[0].
            // Example: "woodandstone:items" + "#" + "WoodenBucket".
            parameters[0] = split[1] + "#" + parameters[0];
        }
        // Otherwise, if the URN's fragment name is empty, then that means this item is using individual textures.
        // Thus, all the necessary is contained in the resource name.
        else
        {
            // First, remove everything up to and including the left parenthesis in the string, and then split it up
            // using commas.
            parameters = urn.getResourceName().toString().split("\\(")[1].split(",");
        }

        // If the number of parameters is less than 6, return with empty.
        if (parameters.length != (isItem ? 6 : 1)) {
            logger.warn("Unexpected number of tokens when trying to getAssetData for a fluid container's content: {}", parameters);
            return Optional.empty();
        }

        // Remove the extraneous right parenthesis from the end of the last parameter.
        parameters[parameters.length-1] = parameters[parameters.length-1].substring(0, parameters[parameters.length-1].length() - 1);

        BufferedImage result;
        if (isItem) {
            String textureWithHole = parameters[0];
            String fluidType = parameters[1];

            BufferedImage fluidTexture = fluidRegistry.getFluidTexture(fluidType);
            if (fluidTexture == null) {
                return Optional.empty();
            }

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

            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
        } else {
            String fluidType = parameters[0];

            result = fluidRegistry.getFluidTexture(fluidType);
            if (result == null) {
                return Optional.empty();
            }
        }

        final ByteBuffer resultBuffer = TextureUtil.convertToByteBuffer(result);

        TextureData data = new TextureData(result.getWidth(), result.getHeight(), new ByteBuffer[]{resultBuffer}, Texture.WrapMode.REPEAT,
                Texture.FilterMode.NEAREST);
        return Optional.of(data);

    }
}
