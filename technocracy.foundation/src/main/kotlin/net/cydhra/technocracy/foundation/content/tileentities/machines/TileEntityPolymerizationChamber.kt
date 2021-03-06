package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_ADDITIVE
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityMultiplierComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.AdditiveConsumptionLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityPolymerizationChamber : MachineTileEntity(), TEInventoryProvider {
    private val inputFluidComponent = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val additiveFluidComponent = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))

    private val additiveMultiplierComponent = TileEntityMultiplierComponent(UPGRADE_ADDITIVE)

    private val outputInventoryComponent = TileEntityInventoryComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    init {
        this.registerComponent(inputFluidComponent, "input")
        this.registerComponent(outputInventoryComponent, "output")
        this.registerComponent(additiveFluidComponent, "additive")
        this.registerComponent(additiveMultiplierComponent, "additive_usage")
        this.registerUpgradeParameter(UPGRADE_ADDITIVE, additiveMultiplierComponent)

        this.addLogicStrategy(AdditiveConsumptionLogic(additiveFluidComponent, 5, additiveMultiplierComponent),
                MACHINE_DEFAULT_CONSUMPTION_LOGIC_NAME)
        this.addLogicStrategy(ItemProcessingLogic(
                RecipeManager.RecipeType.POLYMERIZATION,
                outputInventory = outputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                progress = this.progressComponent,
                baseTickEnergyCost = 100
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return false
    }
}