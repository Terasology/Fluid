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
import org.terasology.registry.Share;
import org.terasology.world.liquid.LiquidType;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles registering and rendering of fluids.
 */
@RegisterSystem
@Share(FluidRegistry.class)
public class FluidRegistryImpl extends BaseComponentSystem implements FluidRegistry {
    private Map<String, FluidRenderer> fluidRenderers = new HashMap<>();
    private Map<LiquidType, String> liquidMapping = new HashMap<>();

    /**
     * Registers the fluid with a given fluid renderer.
     *
     * @param fluidType     the type of fluid
     * @param fluidRenderer the fluid renderer
     * @param liquidType    the liquid type associated with the fluid
     */
    @Override
    public void registerFluid(String fluidType, FluidRenderer fluidRenderer, LiquidType liquidType) {
        fluidRenderers.put(fluidType.toLowerCase(), fluidRenderer);
        if (liquidType != null) {
            liquidMapping.put(liquidType, fluidType.toLowerCase());
        }
    }

    /**
     * Accessor function which returns the fluid type for a given liquid type.
     *
     * @param liquidType the liquid type
     * @return           the fluid type associated with it
     */
    @Override
    public String getFluidType(LiquidType liquidType) {
        return liquidMapping.get(liquidType);
    }

    /**
     * Accessor function which returns the list of fluid renderer associated with a given fluid type.
     *
     * @param fluidType the fluid type
     * @return          the fluid renderer associated with the fluid type
     */
    @Override
    public FluidRenderer getFluidRenderer(String fluidType) {
        return fluidRenderers.get(fluidType.toLowerCase());
    }
}
