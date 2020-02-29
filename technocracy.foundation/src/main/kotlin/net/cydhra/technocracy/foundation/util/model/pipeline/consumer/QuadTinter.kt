package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad

object QuadTinter : IQuadConsumer {
    override var origQuad: BakedQuad? = null
    override var unmodifiedQuad: SimpleQuad? = null

    var tint = -1

    override fun reset() {
        tint = -1
        QuadFacadeTransformer.origQuad = null
    }

    override fun consume(quad: SimpleQuad) {
        if (origQuad!!.hasTintIndex()) {
            quad.tintColor = tint
        }
    }
}