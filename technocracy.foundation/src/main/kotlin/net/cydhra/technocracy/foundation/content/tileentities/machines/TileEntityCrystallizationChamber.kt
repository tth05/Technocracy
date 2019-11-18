package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityCrystallizationChamber : MachineTileEntity(), TEInventoryProvider {

    /**
     * Input inventory for the furnace with one slot
     */
    private val inputFluidComponent = FluidComponent(capacity = 4000,
            allowedFluid = *arrayOf(),
            tanktype = DynamicFluidCapability.TankType.INPUT,
            facing = mutableSetOf(EnumFacing.WEST))

    /**
     * Output inventory for the furnace with one slot
     */
    private val outputInventoryComponent = InventoryComponent(1, this, EnumFacing.EAST)

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.CRYSTALLIZATION) ?: emptyList())
    }

    init {
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.CRYSTALLIZATION,
                inputFluidSlots = arrayOf(this.inputFluidComponent.fluid),
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 60,
                progress = this.progressComponent))
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return inventory == outputInventoryComponent.inventory && this.recipes.any { it.getOutput()[0].item == stack.item }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack) {
    }
}