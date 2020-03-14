package net.cydhra.technocracy.foundation.client.gui.components.heatmeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.util.Interpolator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.roundToInt


class DefaultHeatMeter(posX: Int, posY: Int, val component: HeatStorageTileEntityComponent, val gui: TCGui) : HeatMeter(posX, posY) {
    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1F, 1F, 1F, 1F)

        GlStateManager.color(1F, 1F, 1F, 1F)

        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        drawModalRectWithCustomSizedTexture(x + posX.toDouble(), y + posY.toDouble(), 95.0, 0.0, width.toDouble(), height.toDouble(), 256f, 256f)

        val level = Interpolator.linearInterpolate(lastLevel, this.level, partialTicks)

        val h = height * (1-level)

        drawModalRectWithCustomSizedTexture(x + posX.toDouble(), y + posY + h, 95.0 + width, h, width.toDouble(), height - h, 256f, 256f)
    }

    override fun update() {
        lastLevel = if (lastLevel != -1.0) level else component.heat / component.heatCapacity.toDouble()
        level = component.heat / component.heatCapacity.toDouble()
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "${(level * component.heatCapacity).roundToInt()}mH/${component.heatCapacity}mH"
        gui.drawHoveringText(mutableListOf(str), mouseX, mouseY)
    }
}