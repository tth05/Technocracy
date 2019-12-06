package net.cydhra.technocracy.foundation.model.fx.manager

import net.cydhra.technocracy.foundation.model.fx.api.AbstractParticle
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.profiler.Profiler
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


@SideOnly(Side.CLIENT)
object TCParticleManager {
    val particles = mutableListOf<AbstractParticle>()
    var lastRender = 0

    fun addParticle(p: AbstractParticle) {
        particles.add(p)
    }

    @SubscribeEvent
    fun render(event: RenderGameOverlayEvent.Text) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            event.left.add("")
            event.left.add("TC_Particles: $lastRender/${particles.size}")
        }
    }

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {

        val icamera = Frustum()
        val entity = Minecraft.getMinecraft().renderViewEntity!!
        val d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks.toDouble()
        val d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks.toDouble()
        val d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks.toDouble()
        icamera.setPosition(d0, d1, d2)

        lastRender = 0

        with(Minecraft.getMinecraft().mcProfiler) {
            startSection("TC_Particles")
            particles.filter { icamera.isBoundingBoxInFrustum(it.boundingBox) }.groupBy { it.getType() }.forEach { (type, particles) ->
                startSection(type.name)
                type.preRenderType()
                particles/*.sortedBy { -player.getDistanceSq(it.getPosX(), it.getPosY(), it.getPosZ()) }*/.forEach {
                    lastRender++
                    it.renderParticle(event.partialTicks)
                }
                type.postRenderType()
                endSection()
            }
            endSection()
        }
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (!Minecraft.getMinecraft().isGamePaused) {
                val player = Minecraft.getMinecraft().renderViewEntity ?: return

                with(particles) {
                    removeIf {
                        it.onUpdate()
                        !it.isAlive
                    }
                    sortBy {
                        -player.getDistanceSq(it.getPosX(), it.getPosY(), it.getPosZ())
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun loadWorld(@Suppress("UNUSED_PARAMETER") event: WorldEvent.Load) {
        //clear particles as its per world only
        particles.clear()
    }
}