package org.terasology.fluid.system;

import com.google.common.primitives.UnsignedBytes;
import org.terasology.asset.AssetFactory;
import org.terasology.asset.AssetManager;
import org.terasology.asset.AssetResolver;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.Rect2i;
import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;
import org.terasology.registry.CoreRegistry;
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

            final TextureRegion fluidTexture = CoreRegistry.get(FluidRegistry.class).getFluidRenderer(fluidType).getTexture();
            final TextureData fluidTextureData = fluidTexture.getTexture().getData();
            final ByteBuffer fluidBuffer = fluidTextureData.getBuffers()[0];

            final String[] splitMin = minPerc.split(",");
            final String[] splitSize = sizePerc.split(",");

            TextureRegion containerTexture = Assets.getTextureRegion(textureWithHole);

            TextureData containerTextureData = containerTexture.getTexture().getData();
            ByteBuffer buffer = containerTextureData.getBuffers()[0];

            int stride = containerTextureData.getWidth() * 4;
            int width = containerTexture.getWidth();
            int height = containerTexture.getHeight();
            int posX = containerTexture.getPixelRegion().minX();
            int posY = containerTexture.getPixelRegion().minY();

            int fluidStride = fluidTextureData.getWidth() * 4;
            int fluidWidth = fluidTexture.getWidth();
            int fluidHeight = fluidTexture.getHeight();
            int fluidPosX = fluidTexture.getPixelRegion().minX();
            int fluidPosY = fluidTexture.getPixelRegion().minY();

            Vector2i min = new Vector2i(
                    Math.round(Float.parseFloat(splitMin[0]) * width),
                    Math.round(Float.parseFloat(splitMin[1]) * height));
            Vector2i size = new Vector2i(
                    Math.round(Float.parseFloat(splitSize[0]) * width),
                    Math.round(Float.parseFloat(splitSize[1]) * height));

            Rect2i fillArea = Rect2i.createFromMinAndSize(min, size);

            ByteBuffer data = ByteBuffer.allocateDirect(4 * width * height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (fillArea.contains(x, y)) {
                        final int fluidX = (x - min.x) % fluidWidth;
                        final int fluidY = (y - min.y) % fluidHeight;

                        int fluidR = UnsignedBytes.toInt(fluidBuffer.get((fluidPosY + fluidY) * fluidStride + (fluidPosX + fluidX) * 4));
                        int fluidG = UnsignedBytes.toInt(fluidBuffer.get((fluidPosY + fluidY) * fluidStride + (fluidPosX + fluidX) * 4 + 1));
                        int fluidB = UnsignedBytes.toInt(fluidBuffer.get((fluidPosY + fluidY) * fluidStride + (fluidPosX + fluidX) * 4 + 2));
                        int fluidA = UnsignedBytes.toInt(fluidBuffer.get((fluidPosY + fluidY) * fluidStride + (fluidPosX + fluidX) * 4 + 3));

                        int imageR = UnsignedBytes.toInt(buffer.get((posY + y) * stride + (posX + x) * 4));
                        int imageG = UnsignedBytes.toInt(buffer.get((posY + y) * stride + (posX + x) * 4 + 1));
                        int imageB = UnsignedBytes.toInt(buffer.get((posY + y) * stride + (posX + x) * 4 + 2));
                        int imageA = UnsignedBytes.toInt(buffer.get((posY + y) * stride + (posX + x) * 4 + 3));

                        data.put(mergeColor(fluidR, imageR, fluidA / 255f, imageA / 255f));
                        data.put(mergeColor(fluidG, imageG, fluidA / 255f, imageA / 255f));
                        data.put(mergeColor(fluidB, imageB, fluidA / 255f, imageA / 255f));
                        data.put(mergeAlpha(fluidA / 255f, imageA / 255f));
                    } else {
                        data.put(buffer.get((posY + y) * stride + (posX + x) * 4));
                        data.put(buffer.get((posY + y) * stride + (posX + x) * 4 + 1));
                        data.put(buffer.get((posY + y) * stride + (posX + x) * 4 + 2));
                        data.put(buffer.get((posY + y) * stride + (posX + x) * 4 + 3));
                    }
                }
            }
            data.rewind();

            return factory.buildAsset(uri, new TextureData(width, height, new ByteBuffer[]{data}, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));
        }

        private byte mergeColor(int background, int foreground, float backgroundAlpha, float foregroundAlpha) {
            return UnsignedBytes.checkedCast(TeraMath.clamp(
                    Math.round(foreground * foregroundAlpha + (background * backgroundAlpha * (1 - foregroundAlpha))), 0, 255));
        }

        private byte mergeAlpha(float backgroundAlpha, float foregroundAlpha) {
            return UnsignedBytes.checkedCast(TeraMath.clamp(
                    Math.round(255f * (foregroundAlpha + (backgroundAlpha * (1 - foregroundAlpha)))), 0, 255));
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
