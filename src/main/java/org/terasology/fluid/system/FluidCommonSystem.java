package org.terasology.fluid.system;

import org.terasology.asset.AssetFactory;
import org.terasology.asset.AssetManager;
import org.terasology.asset.AssetResolver;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureData;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.world.liquid.LiquidType;

import java.awt.*;
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
            if (parts.length == 2) {
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
            split[1] = split[1].substring(0, split[1].length() - 1);

            TextureRegion containerTexture = Assets.getTextureRegion(split[1]);

            TextureData containerTextureData = containerTexture.getTexture().getData();
            ByteBuffer buffer = containerTextureData.getBuffers()[0];
            int width = containerTexture.getWidth();
            int height = containerTexture.getHeight();
            int bytesPerPixel = 4;
            int stride = containerTextureData.getWidth() * bytesPerPixel;

            int posX = containerTexture.getPixelRegion().minX();
            int posY = containerTexture.getPixelRegion().minY();

            ByteBuffer data = ByteBuffer.allocateDirect(4 * width * height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    for (int i = 0; i < bytesPerPixel; i++) {
                        data.put(buffer.get((posY + y) * stride + (posX + x) * bytesPerPixel + i));
                    }
                }
            }
            data.rewind();

            return factory.buildAsset(uri, new TextureData(width, height, new ByteBuffer[]{data}, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));
        }

        public byte[] copy(ByteBuffer b) {
            byte[] oldBytes = b.array();
            byte[] copiedBytes = new byte[oldBytes.length];
            // (Object src, int srcPos, Object dest, int destPos, int length)
            System.arraycopy(oldBytes, 0, copiedBytes, 0, oldBytes.length);
            return copiedBytes;
        }
    }
}
