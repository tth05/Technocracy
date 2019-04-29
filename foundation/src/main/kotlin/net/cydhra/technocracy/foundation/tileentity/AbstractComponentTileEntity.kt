package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.components.AbstractCapabilityComponent
import net.cydhra.technocracy.foundation.tileentity.components.IComponent
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability


abstract class AbstractComponentTileEntity : TileEntity() {

    /**
     * All machine components that are saved to NBT and possibly accessible from GUI
     */
    private val components: MutableList<Pair<String, IComponent>> = mutableListOf()

    /**
     * All components that also offer a capability. They must also be added to [components] but for speed they are
     * also collected in this list for quick query times in [hasCapability]
     */
    private val capabilityComponents: MutableSet<AbstractCapabilityComponent> = mutableSetOf()

    /**
     * The attached block's BlockState.
     */
    protected var state: IBlockState? = null

    fun getComponents(): MutableList<Pair<String, IComponent>> {
        return this.components
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        for ((name, component) in components) {
            compound.setTag(name, component.serializeNBT())
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        for ((name, component) in components) {
            if (compound.hasKey(name))
                component.deserializeNBT(compound.getTag(name))
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capabilityComponents.any { it.hasCapability(capability, facing) }
                || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return capabilityComponents
                .firstOrNull { it.hasCapability(capability, facing) }
                ?.getCapability(capability, facing) ?: super.getCapability(capability, facing)
    }

    /**
     * Register a machine component. Should happen during construction of the tile entity instance.
     *
     * @param component [IComponent] implementation
     * @param name machine-unique name for the component. Used in NBT serialization
     */
    protected fun registerComponent(component: IComponent, name: String) {
        this.components += name to component

        if (component is AbstractCapabilityComponent) {
            capabilityComponents += component
        }
    }

    /**
     * Query the world for the [IBlockState] associated with this entity
     *
     * @return the block state of the associated block in world
     */
    fun getBlockState(): IBlockState {
        if (this.state == null) {
            this.state = this.world.getBlockState(this.getPos())
        }
        return this.state!!
    }

    /**
     * Mark the block for a block update. Does not mark the chunk dirty.
     */
    fun markForUpdate() {
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3)
        }
    }
}