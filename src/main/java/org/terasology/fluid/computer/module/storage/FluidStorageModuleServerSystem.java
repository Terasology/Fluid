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
package org.terasology.fluid.computer.module.storage;

import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.system.FluidManager;
import org.terasology.fluid.system.FluidUtils;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.mobileBlocks.server.AfterBlockMovedEvent;
import org.terasology.mobileBlocks.server.BeforeBlockMovesEvent;
import org.terasology.mobileBlocks.server.BlockTransitionDuringMoveEvent;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.items.OnBlockToItem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class FluidStorageModuleServerSystem extends BaseComponentSystem {
    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private EntityManager entityManager;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private FluidManager fluidManager;

    private boolean computerIsMoving;

    @ReceiveEvent
    public void computerModuleSlotChanged(InventorySlotChangedEvent event, EntityRef computerEntity, ComputerComponent computer) {
        // If computer is moving, we have to preserve the component to be able to copy it's data, so do not remove the
        // component at this point, the entity will be destroyed upon movement finish anyway
        if (!computerIsMoving) {
            ComputerModuleComponent oldModule = event.getOldItem().getComponent(ComputerModuleComponent.class);
            if (oldModule != null && oldModule.moduleType.equals(FluidStorageModuleCommonSystem.COMPUTER_FLUID_STORAGE_MODULE_TYPE)) {
                EntityRef inventoryEntity = computerEntity.getComponent(FluidInternalStorageComponent.class).inventoryEntity;
                inventoryEntity.destroy();
                computerEntity.removeComponent(FluidInternalStorageComponent.class);
            }
        }

        ComputerModuleComponent newModule = event.getNewItem().getComponent(ComputerModuleComponent.class);
        if (newModule != null && newModule.moduleType.equals(FluidStorageModuleCommonSystem.COMPUTER_FLUID_STORAGE_MODULE_TYPE)) {
            FluidStorageComputerModule computerModuleByType = (FluidStorageComputerModule) computerModuleRegistry.getComputerModuleByType(newModule.moduleType);

            EntityRef storageEntity = entityManager.create();

            FluidInternalStorageComponent internalStorage = new FluidInternalStorageComponent();
            internalStorage.inventoryEntity = storageEntity;

            FluidInventoryComponent inventoryComponent = new FluidInventoryComponent(computerModuleByType.getSlotCount(), computerModuleByType.getMaxVolume());
            storageEntity.addComponent(inventoryComponent);

            computerEntity.addComponent(internalStorage);
        }
    }

    @ReceiveEvent
    public void beforeComputerMoveSetFlag(BeforeBlockMovesEvent event, EntityRef entity, ComputerComponent component) {
        computerIsMoving = true;
    }

    @ReceiveEvent
    public void afterComputerMoveResetFlag(AfterBlockMovedEvent event, EntityRef entity, ComputerComponent component) {
        computerIsMoving = false;
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void computerMovedCopyInternalStorage(BlockTransitionDuringMoveEvent event, EntityRef entity, FluidInternalStorageComponent storage) {
        EntityRef inventoryEntity = storage.inventoryEntity;
        EntityRef newInventoryEntity = event.getIntoEntity().getComponent(FluidInternalStorageComponent.class).inventoryEntity;

        int slotCount = FluidUtils.getFluidSlotCount(inventoryEntity);
        // We assume the number of slots does not change
        for (int i = 0; i < slotCount; i++) {
            float volume = FluidUtils.getFluidAmount(inventoryEntity, i);
            String fluidType = FluidUtils.getFluidAt(inventoryEntity, i);
            fluidManager.moveFluid(null, inventoryEntity, newInventoryEntity, i, fluidType, i, volume);
        }
    }


    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void computerDestroyed(OnBlockToItem event, EntityRef computerEntity, ComputerComponent computer) {
        InventoryComponent component = computerEntity.getComponent(InventoryComponent.class);
        for (EntityRef module : component.itemSlots) {
            if (module.exists() && module.getComponent(ComputerModuleComponent.class).moduleType.equals(FluidStorageModuleCommonSystem.COMPUTER_FLUID_STORAGE_MODULE_TYPE)) {
                EntityRef inventoryEntity = computerEntity.getComponent(FluidInternalStorageComponent.class).inventoryEntity;
                inventoryEntity.destroy();
            }
        }
    }
}
