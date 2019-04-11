package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.items.DustItem
import net.cydhra.technocracy.foundation.items.SheetItem
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.minecraft.creativetab.CreativeTabs

val coalDustItem = DustItem("coal", ConstantItemColor(0x2f2f2f))
val ironDustItem = DustItem("iron", ConstantItemColor(0xD4D4CD))
val ironSheetItem = SheetItem("iron", ConstantItemColor(0xD4D4CD))

val batteryItem = BaseItem("battery").apply { creativeTab = CreativeTabs.MATERIALS }
val akkumulatorItem = BaseItem("akkumulator").apply { creativeTab = CreativeTabs.MATERIALS }