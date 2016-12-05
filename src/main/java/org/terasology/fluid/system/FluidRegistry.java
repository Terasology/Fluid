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

import org.terasology.world.liquid.LiquidType;

/**
 * A generic fluid registry interface.
 */
public interface FluidRegistry {
    /**
     * Registers a fluid with a fluid renderer.
     *
     * @param fluidType     The type of fluid
     * @param fluidRenderer The fluid renderer
     * @param liquidType    The liquid type associated with the fluid
     */
    void registerFluid(String fluidType, FluidRenderer fluidRenderer, LiquidType liquidType);

    /**
     * Accessor function which returns the list of fluid renderer associated with a given fluid type.
     *
     * @param fluidType The fluid type
     */
    FluidRenderer getFluidRenderer(String fluidType);

    /**
     * Accessor function which returns the fluid type for a given liquid type.
     *
     * @param liquidType The liquid type
     */
    String getFluidType(LiquidType liquidType);
}
