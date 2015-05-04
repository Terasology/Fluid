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

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.fluid.system.FluidManager;
import org.terasology.world.BlockEntityRegistry;

public class FluidManipulatorComputerModule extends DefaultComputerModule {
    public FluidManipulatorComputerModule(FluidManipulatorConditionsRegister fluidManipulatorConditionsRegister, FluidManager fluidManager,
                                          BlockEntityRegistry blockEntityRegistry, String moduleType, String moduleName) {
        super(moduleType, moduleName);

        addMethod("getInputInventoryBinding", new FluidInventoryBindingMethod("getInputInventoryBinding", blockEntityRegistry, true));
        addMethod("getOutputInventoryBinding", new FluidInventoryBindingMethod("getOutputInventoryBinding", blockEntityRegistry, true));

        addMethod("getMaximumVolume", new FluidGetMaximumVolumeMethod("getMaximumVolume"));
        addMethod("getStoredVolume", new FluidGetStoredVolumeMethod("getStoredVolume"));
        addMethod("getFluidType", new FluidGetTypeMethod("getFluidType"));

        addMethod("move", new FluidMoveMethod("move", fluidManager));
        addMethod("getFluidsAndChangeCondition", new FluidInventoryAndChangedConditionMethod("getFluidsAndChangeCondition", fluidManipulatorConditionsRegister));
    }
}
