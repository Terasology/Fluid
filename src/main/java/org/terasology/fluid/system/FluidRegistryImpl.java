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
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterSystem
@Share(FluidRegistry.class)
public class FluidRegistryImpl extends BaseComponentSystem implements FluidRegistry {
    private Map<String, FluidRenderer> fluidRenderers = new HashMap<>();
    private Map<LiquidType, String> liquidMapping = new HashMap<>();

    @Override
    public void registerFluid(String fluidType, FluidRenderer fluidRenderer, LiquidType liquidType) {
        fluidRenderers.put(fluidType.toLowerCase(), fluidRenderer);
        if (liquidType != null) {
            liquidMapping.put(liquidType, fluidType.toLowerCase());
        }
    }

    @Override
    public String getFluidType(LiquidType liquidType) {
        return liquidMapping.get(liquidType);
    }

    @Override
    public FluidRenderer getFluidRenderer(String fluidType) {
        return fluidRenderers.get(fluidType.toLowerCase());
    }
}
