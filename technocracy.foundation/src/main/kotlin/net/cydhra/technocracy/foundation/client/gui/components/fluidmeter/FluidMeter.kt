package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.util.math.MathHelper

abstract class FluidMeter(val posX: Int, val posY: Int) : TCComponent() {

    /**
     * fluid level from 0.0 to 1.0
     */
    var level = 0.0f

    override var width = 16
    override var height = 50

    override fun update() {
        this.level += 0.01F
        this.level = MathHelper.clamp(this.level, 0F, 1F)

        if (level == 1.0F) {
            this.level = 0F
        }
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {

    }

    abstract fun drawOverlay(x: Int, y: Int)
    abstract fun drawBackground(x: Int, y: Int)
}
