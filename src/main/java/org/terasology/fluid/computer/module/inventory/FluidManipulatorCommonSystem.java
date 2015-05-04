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
package org.terasology.fluid.computer.module.inventory;

import org.terasology.browser.data.basic.HTMLLikeParser;
import org.terasology.computer.system.common.ComputerLanguageRegistry;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.system.FluidManager;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;

@RegisterSystem(RegisterMode.ALWAYS)
public class FluidManipulatorCommonSystem extends BaseComponentSystem {
    public static final String COMPUTER_FLUID_MODULE_TYPE = "FluidManipulator";

    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private FluidManager fluidManager;
    @In
    private FluidManipulatorConditionsRegister fluidManipulatorConditionsRegister;
    @In
    private ComputerLanguageRegistry computerLanguageRegistry;

    @Override
    public void preBegin() {
        // Dependency on computers is optional, so just don't call it, if it's not there
        if (computerLanguageRegistry != null && computerModuleRegistry != null) {
            computerLanguageRegistry.registerObjectType(
                    "FluidInventoryBinding",
                    HTMLLikeParser.parseHTMLLike(null, "An object that tells a method how to access a fluid inventory. Usually used as a parameter " +
                            "for methods in Fluid Manipulator computer module. This object comes in two types defined upon creation:<l>" +
                            "* input - that allows to place fluids in the specified inventory,<l>" +
                            "* output - that allows to extract fluids from the specified inventory.<l>" +
                            "Attempting to use an incorrect type as a parameter of a method will result in an ExecutionException."));

            computerModuleRegistry.registerComputerModule(
                    COMPUTER_FLUID_MODULE_TYPE,
                    new FluidManipulatorComputerModule(fluidManipulatorConditionsRegister, fluidManager,
                            blockEntityRegistry, COMPUTER_FLUID_MODULE_TYPE, "Fluid manipulator"),
                    "This module allows computer to manipulate fluids.",
                    null);
        }
    }
}
