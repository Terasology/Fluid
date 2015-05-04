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
import org.terasology.computer.module.inventory.InventoryBinding;
import org.terasology.computer.module.inventory.InventoryModuleConditionsRegister;
import org.terasology.computer.module.inventory.InventoryModuleUtils;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.system.FluidUtils;
import org.terasology.logic.inventory.InventoryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidInventoryAndChangedConditionMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private FluidManipulatorConditionsRegister fluidManipulatorConditionsRegister;

    public FluidInventoryAndChangedConditionMethod(String methodName, FluidManipulatorConditionsRegister fluidManipulatorConditionsRegister) {
        super("Gets the information about fluids stored in the inventory as well as a Condition " +
                        "that allows to wait for the inventory's contents to be changed.", "Map",
                "Map containing to entries:\n" +
                        "- \"inventory\" - containing a List of Maps, with each entry in the list corresponding to one slot " +
                        "in the fluid inventory, and each entry Map containing two keys - \"type\" with String value of fluid type, " +
                        "as specified in the getFluidType() method, and \"volume\" with Number value, specifying volume of the fluid in that slot\n" +
                        "- \"condition\" - containing condition you could wait on to listen on a change of the fluid inventory from " +
                        "the state described in the \"inventory\" key. Please note, that the condition might be fulfilled even though " +
                        "the fluid inventory state has not changed.");
        this.fluidManipulatorConditionsRegister = fluidManipulatorConditionsRegister;
        this.methodName = methodName;

        addParameter("fluidInventoryBinding", "FluidInventoryBinding", "Fluid inventory it should get contents and change condition for.");
    }

    @Override
    public int getCpuCycleDuration() {
        return 200;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventory = FluidFunctionParamValidationUtil.validateFluidInventoryBinding(line, computer,
                parameters, "fluidInventoryBinding", methodName, null);

        Map<String, Variable> result = new HashMap<>();

        List<Variable> inventoryResult = new ArrayList<>();

        for (int slot : inventory.slots) {
            String fluidType = FluidUtils.getFluidAt(inventory.inventory, slot);
            float fluidAmount = FluidUtils.getFluidAmount(inventory.inventory, slot);

            Map<String, Variable> itemMap = new HashMap<>();

            itemMap.put("type", new Variable(fluidType));
            itemMap.put("volume", new Variable(fluidAmount));

            inventoryResult.add(new Variable(itemMap));
        }

        result.put("inventory", new Variable(inventoryResult));

        result.put("condition", new Variable(fluidManipulatorConditionsRegister.registerFluidInventoryChangeListener(inventory.inventory)));

        return result;
    }
}
