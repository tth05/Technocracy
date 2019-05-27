package net.cydhra.technocracy.foundation.capabilities.fluid

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties


class DynamicFluidHandler(var capacity: Int = 1000, val allowedFluid: MutableList<String>,
                          var tanktype: TankType = TankType.BOTH) :
        IFluidHandler, INBTSerializable<NBTTagCompound> {

    var currentFluid: FluidStack? = null
        private set

    val simpleTankProperty = arrayOf<IFluidTankProperties>(SimpleTankProperty(this))

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if (currentFluid == null || !currentFluid!!.isFluidEqual(resource)) {
            return null
        }

        val drain = Math.min(resource.amount, currentFluid!!.amount)

        if (doDrain) {
            currentFluid!!.amount -= drain
        }

        val out = FluidStack(currentFluid!!.fluid, drain)

        if (currentFluid!!.amount == 0) {
            currentFluid = null
        }

        return out
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (currentFluid == null)
            return null

        val drain = Math.min(maxDrain, currentFluid!!.amount)

        if (doDrain) {
            currentFluid!!.amount -= drain
        }

        val out = FluidStack(currentFluid!!.fluid, drain)

        if (currentFluid!!.amount == 0) {
            currentFluid = null
        }

        return out
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        if (!allowedFluid.contains(resource.fluid.name) && !allowedFluid.isEmpty()) {
            return 0
        }

        if (currentFluid != null && !currentFluid!!.isFluidEqual(resource)) {
            return 0
        }


        if (doFill) {
            if (currentFluid == null) {
                currentFluid = FluidStack(resource.fluid, 0)
            }

            val fill = Math.min(resource.amount, capacity - currentFluid!!.amount)

            currentFluid!!.amount += fill
            return fill
        } else {
            if (currentFluid == null) {
                return Math.min(resource.amount, capacity)
            }

            return Math.min(resource.amount, capacity - currentFluid!!.amount)
        }
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return simpleTankProperty
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        currentFluid = FluidStack.loadFluidStackFromNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound {
        if (currentFluid != null) {
            return currentFluid!!.writeToNBT(NBTTagCompound())
        }
        return NBTTagCompound()
    }

    enum class TankType {
        INPUT, OUTPUT, BOTH
    }
}