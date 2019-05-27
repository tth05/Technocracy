package net.cydhra.technocracy.foundation.client.textures

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class TextureAtlasManager {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    companion object {
        lateinit var connector_energy: TextureAtlasSprite
        lateinit var connector_inventory: TextureAtlasSprite
        lateinit var pipe_item: TextureAtlasSprite
        lateinit var pipe_fluid: TextureAtlasSprite
        lateinit var pipe_energy: TextureAtlasSprite
        lateinit var pipe_node: TextureAtlasSprite
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerTextureAtlas(event: TextureStitchEvent.Pre) {
        connector_energy = event.map.registerSprite(getIcon("extra/connector_energy"))
        connector_inventory = event.map.registerSprite(getIcon("extra/connector_inventory"))
        pipe_item = event.map.registerSprite(getIcon("block/steel"))
        pipe_fluid = event.map.registerSprite(getIcon("block/steel_dark"))
        pipe_energy = event.map.registerSprite(getIcon("block/boiler_wall"))
        pipe_node = event.map.registerSprite(getIcon("block/frame_corners"))
    }

    fun getIcon(name: String): ResourceLocation {
        return ResourceLocation("technocracy.foundation", name)
    }
}