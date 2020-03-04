package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.cydhra.technocracy.foundation.util.compound
import net.minecraft.nbt.NBTTagCompound


class IntegerDataTileEntityComponent : AbstractTileEntityComponent() {

    var value: Int = 0
        private set

    fun setValue(newValue: Int) {
        value = newValue
        markDirty(false)
    }

    override fun serializeNBT(): NBTTagCompound {
        return compound {
            "value" to value
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("value"))
            value = nbt.getInteger("value")
    }

    override val type: ComponentType = ComponentType.OTHER
}