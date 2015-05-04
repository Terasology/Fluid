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

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.module.inventory.RelativeInventoryBindingCustomObject;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.math.Direction;
import org.terasology.world.BlockEntityRegistry;

import java.util.Map;

public class FluidInventoryBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private BlockEntityRegistry blockEntityRegistry;
    private boolean input;

    public FluidInventoryBindingMethod(String methodName, BlockEntityRegistry blockEntityRegistry, boolean input) {
        super(input ? "Creates the input fluid inventory binding for the storage specified in the direction. " +
                        "This binding allows to insert fluids into the inventory only." :
                        "Creates the output fluid inventory binding for the storage specified in the direction. " +
                        "This binding allows to remove fluids from the inventory only.", "FluidInventoryBinding",
                input ? "Input binding for the direction specified." : "Output binding for the direction specified.");
        this.blockEntityRegistry = blockEntityRegistry;
        this.input = input;
        this.methodName = methodName;

        addParameter("direction", "Direction", "Direction in which the fluid manipulator is bound to.");

        if (input) {
            addExample("This example creates input fluid inventory binding to an inventory above it and prints out the slot count for it. Please make sure " +
                            "this computer has a module of Fluid Manipulator type in any of its slots.",
                    "var invBind = computer.bindModuleOfType(\"" + FluidManipulatorCommonSystem.COMPUTER_FLUID_MODULE_TYPE + "\");\n" +
                            "var topInv = invBind."+methodName+"(\"up\");\n" +
                            "console.append(\"Inventory above has \" + invBind.getInventorySlotCount(topInv) + \" number of slots available for input.\");"
            );
        } else {
            addExample("This example creates output fluid inventory binding to an inventory above it and prints out the slot count for it. Please make sure " +
                            "this computer has a module of Fluid Manipulator type in any of its slots.",
                    "var invBind = computer.bindModuleOfType(\"" + FluidManipulatorCommonSystem.COMPUTER_FLUID_MODULE_TYPE + "\");\n" +
                            "var topInv = invBind."+methodName+"(\"up\");\n" +
                            "console.append(\"Inventory above has \" + invBind.getInventorySlotCount(topInv) + \" number of slots available for output.\");"
            );
        }
    }

    @Override
    public int getCpuCycleDuration() {
        return 10;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters,
                "direction", methodName);

        return new RelativeFluidInventoryBindingCustomObject(blockEntityRegistry, direction, input);
    }
}
