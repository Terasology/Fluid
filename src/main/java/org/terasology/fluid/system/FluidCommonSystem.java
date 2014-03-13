package org.terasology.fluid.system;

import org.terasology.asset.AssetFactory;
import org.terasology.asset.AssetManager;
import org.terasology.asset.AssetResolver;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureData;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.world.liquid.LiquidType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterSystem
public class FluidCommonSystem extends BaseComponentSystem {
    @In
    private FluidRegistry fluidRegistry;
    @In
    private AssetManager assetManager;

    @Override
    public void preBegin() {
        AssetUri waterTextureUri = TextureUtil.getTextureUriForColor(Color.BLUE);
        fluidRegistry.registerFluid("Fluid:Water", new TextureFluidRenderer(Assets.getTexture(waterTextureUri.toNormalisedSimpleString()), "water"), LiquidType.WATER);

        assetManager.addResolver(AssetType.TEXTURE, new FluidContainerAssetResolver());
    }

    public static class FluidContainerAssetResolver implements AssetResolver<Texture, TextureData> {
        @Override
        public AssetUri resolve(String partialUri) {

            String[] parts = partialUri.split("\\(", 2);
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
            if (!"fluid".equals(uri.getNormalisedModuleName())) {
                return null;
            }
            String assetName = uri.getAssetName();
            String[] split = assetName.split("\\(");

            // Remove last parenthesis
            String textureWithHole = split[1].substring(0, split[1].length() - 1);
            String fluidType = split[2].substring(0, split[2].length() - 1);
            String minPerc = split[3].substring(0, split[3].length() - 1);
            String sizePerc = split[4].substring(0, split[4].length() - 1);

            BufferedImage fluidTexture = TextureUtil.convertToImage(CoreRegistry.get(FluidRegistry.class).getFluidRenderer(fluidType).getTexture());

            final String[] splitMin = minPerc.split(",");
            final String[] splitSize = sizePerc.split(",");

            BufferedImage containerTexture = TextureUtil.convertToImage(Assets.getTextureRegion(textureWithHole));
            int width = containerTexture.getWidth();
            int height = containerTexture.getHeight();

            Vector2i min = new Vector2i(
                    Math.round(Float.parseFloat(splitMin[0]) * width),
                    Math.round(Float.parseFloat(splitMin[1]) * height));
            Vector2i size = new Vector2i(
                    Math.round(Float.parseFloat(splitSize[0]) * width),
                    Math.round(Float.parseFloat(splitSize[1]) * height));

            BufferedImage result = new BufferedImage(containerTexture.getWidth(), containerTexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) result.getGraphics();
            try {
                // Draw fluid texture tiled in the designated space
                for (int x = min.x; x < min.x + size.x; x += size.x) {
                    for (int y = min.y; y < min.y + size.y; y += size.y) {
                        int fluidTileWidth = Math.min(size.x, size.x + min.x - x);
                        int fluidTileHeight = Math.min(size.y, size.y + min.y - y);
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
}
