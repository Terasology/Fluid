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
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.fluid.system.FluidManager;
import org.terasology.fluid.system.FluidUtils;

import java.util.Map;

public class FluidMoveMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private FluidManager fluidManager;

    public FluidMoveMethod(String methodName, FluidManager fluidManager) {
        super("Moves fluid at the specified slot in the \"from\" fluid inventory to the \"to\" fluid inventory.", "Number",
                "Amount of fluid moved.");
        this.fluidManager = fluidManager;
        this.methodName = methodName;

        addParameter("fluidInventoryBindingFrom", "FluidInventoryBinding", "Fluid inventory it should extract the fluid from.");
        addParameter("fluidInventoryBindingTo", "FluidInventoryBinding", "Fluid inventory it should insert the fluid to.");
        addParameter("slotFrom", "Number", "Slot number of the \"from\" fluid inventory it should extract fluid from.");
        addParameter("slotTo", "Number", "Slot number of the \"to\" fluid inventory it should insert the fluid to.");
        addParameter("volume", "Number", "Amount of fluid to move.");
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventoryFrom = FluidFunctionParamValidationUtil.validateFluidInventoryBinding(line, computer,
                parameters, "fluidInventoryBindingFrom", methodName, false);
        InventoryBinding.InventoryWithSlots inventoryTo = FluidFunctionParamValidationUtil.validateFluidInventoryBinding(line, computer,
                parameters, "fluidInventoryBindingTo", methodName, true);

        int slotFrom = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventoryFrom, "slotFrom", methodName);
        int slotTo = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventoryTo, "slotTo", methodName);

        float volume = FunctionParamValidationUtil.validateFloatParameter(line, parameters, "volume", methodName);

        int realSlotFrom = inventoryFrom.slots.get(slotFrom);
        int realSlotTo = inventoryTo.slots.get(slotTo);

        String fluidType = FluidUtils.getFluidAt(inventoryFrom.inventory, realSlotFrom);
        if (fluidType == null) {
            return 0;
        }

        return fluidManager.moveFluid(computer.getComputerEntity(), inventoryFrom.inventory, inventoryTo.inventory, realSlotFrom, fluidType, realSlotTo, volume);
    }
}
