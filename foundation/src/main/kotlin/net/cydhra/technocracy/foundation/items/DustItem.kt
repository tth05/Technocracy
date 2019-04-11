package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.minecraft.creativetab.CreativeTabs

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
open class DustItem(ingotMaterialName: String, color: ConstantItemColor)
    : BaseItem("dust.$ingotMaterialName", oreDictName = "dust${ingotMaterialName.capitalize()}", itemColor = color) {

    override val modelLocation: String = "${TCFoundation.MODID}:dust"

    init {
        this.creativeTab = CreativeTabs.MATERIALS
    }
}