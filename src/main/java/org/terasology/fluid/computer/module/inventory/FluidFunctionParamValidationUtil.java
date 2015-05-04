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

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.inventory.InventoryBinding;

import java.util.Map;

public class FluidFunctionParamValidationUtil {
    private FluidFunctionParamValidationUtil() { }

    public static InventoryBinding.InventoryWithSlots validateFluidInventoryBinding(
            int line, ComputerCallback computer, Map<String, Variable> parameters,
            String parameterName, String functionName, Boolean input) throws ExecutionException {
        Variable inventoryBinding = FunctionParamValidationUtil.validateParameter(line, parameters, parameterName, functionName, Variable.Type.CUSTOM_OBJECT);
        CustomObject customObject = (CustomObject) inventoryBinding.getValue();
        if (!customObject.getType().contains("FLUID_INVENTORY_BINDING")
                || (input != null && input != ((InventoryBinding) customObject).isInput()))
            throw new ExecutionException(line, "Invalid " + parameterName + " in " + functionName + "()");

        InventoryBinding binding = (InventoryBinding) inventoryBinding.getValue();
        return binding.getInventoryEntity(line, computer);
    }
}
