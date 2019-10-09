package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe data model for recipes that convert one single [FluidStack] into one single output [FluidStack]
 *
 * @param inputFluid input [FluidStack]
 * @param outputFluid output [FluidStack]
 * @param processingCost amount of processing the machine has to solve for this recipe
 */
class FluidToFluidRecipe(
        val inputFluid: FluidStack,
        val outputFluid: FluidStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return fluids.size == 1 && this.inputFluid.isFluidEqual(inputFluid)
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(this.inputFluid)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(this.outputFluid)
    }
}