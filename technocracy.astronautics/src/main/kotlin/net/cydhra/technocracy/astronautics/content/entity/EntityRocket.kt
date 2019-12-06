package net.cydhra.technocracy.astronautics.content.entity


import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.astronautics.content.blocks.rocketDriveBlock
import net.cydhra.technocracy.astronautics.content.fx.ParticleSmoke
import net.cydhra.technocracy.astronautics.content.tileentity.TileEntityRocketController
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.OwnerShipComponent
import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager
import net.cydhra.technocracy.foundation.util.readCompoundTag
import net.cydhra.technocracy.foundation.util.structures.Template
import net.cydhra.technocracy.foundation.util.writeCompoundTag
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.DamageSource
import net.minecraft.util.EntitySelectors
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.max
import kotlin.math.min
import net.minecraftforge.event.world.GetCollisionBoxesEvent as GetCollisionBoxesEvent1

open class EntityRocket(world: World) : Entity(world), IEntityAdditionalSpawnData {

    constructor(world: World, template: Template, controllerBlock: BlockPos, owner: GroupManager.PlayerGroup) : this(world) {
        this.template = template
        this.controllerBlock = controllerBlock

        with(this.owner) {
            allowAutoSave = false
            setOwnerShip(owner)
        }

        tank.allowAutoSave = false
    }

    @SideOnly(Side.CLIENT)
    var vbo: VertexBuffer? = null

    lateinit var controllerBlock: BlockPos
    private val LIFTOFF = EntityDataManager.createKey(EntityRocket::class.java, DataSerializers.BOOLEAN)

    val owner = OwnerShipComponent()
    //TODO config
    val tank = FluidComponent(DynamicFluidCapability(0, mutableListOf("rocket_fuel")), EnumFacing.values().toMutableSet())

    var liftOff: Boolean
        get() = dataManager.get(LIFTOFF)
        set(value) {
            if (!liftOff) {
                this.motionY = 0.005
            }
            dataManager.set(LIFTOFF, value)
        }


    override fun writeEntityToNBT(compound: NBTTagCompound) {
        with(compound) {
            setTag("blocks", template.serializeNBT())
            setTag("controller", NBTUtil.createPosTag(controllerBlock))
            setTag("owner", owner.serializeNBT())
            setTag("tank", tank.serializeNBT())
        }
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        with(compound) {
            template.deserializeNBT(getCompoundTag("blocks"))
            controllerBlock = NBTUtil.getPosFromTag(getCompoundTag("controller"))
            owner.deserializeNBT(getCompoundTag("owner"))
            tank.deserializeNBT(getCompoundTag("tank"))
        }
    }

    var template: Template = Template()
    lateinit var entityBox: AxisAlignedBB
    var lastBB: List<AxisAlignedBB> = mutableListOf()

    override fun entityInit() {
    }

    init {
        dataManager.register(LIFTOFF, false)
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun readSpawnData(additionalData: ByteBuf?) {
        if (additionalData == null) return
        template.deserializeNBT(readCompoundTag(additionalData))
    }

    override fun writeSpawnData(buffer: ByteBuf?) {
        if (buffer == null) return
        writeCompoundTag(template.serializeNBT(), buffer)
    }

    @SideOnly(Side.CLIENT)
    override fun onRemovedFromWorld() {
        if (vbo != null)
            vbo!!.deleteGlBuffers()

        if (::controllerBlock.isInitialized)
            (world.getTileEntity(controllerBlock) as? TileEntityRocketController)?.unlinkRocket()

        super.onRemovedFromWorld()
    }

    override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean {
        if (source.isCreativePlayer)
            world.removeEntity(this)

        return super.attackEntityFrom(source, amount)
    }

    override fun onUpdate() {
        setNoGravity(true)
        noClip = false

        this.motionX = 0.0

        if (liftOff) {
            if (ticksExisted % 4 == 0)
                this.motionY *= 1.08
        } else {
            this.motionY = 0.0
        }

        if (posY > 300)
            world.removeEntity(this)

        super.onUpdate()


        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        val d0 = this.motionX
        val d1 = this.motionY
        val d2 = this.motionZ

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)

        if (!this.world.isRemote) {
            val d3 = this.motionX - d0
            val d4 = this.motionY - d1
            val d5 = this.motionZ - d2
            val d6 = d3 * d3 + d4 * d4 + d5 * d5

            if (d6 > 0.01) {
                this.isAirBorne = true
            }
        }

        val list = this.world.getEntitiesInAABBexcluding(this, this.entityBoundingBox, EntitySelectors.getTeamCollisionPredicate(this))
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity is EntityPlayer)
                    this.collideWithEntity(entity)
            }
        }

        if (world.isRemote) {
            for (info in template.blocks) {
                if (info.pos.y == 0) {
                    if (info.block == rocketDriveBlock) {
                        TCParticleManager.addParticle(ParticleSmoke(world, posX + info.pos.x + rand.nextFloat() - 0.5f, posY - rand.nextFloat(), posZ + info.pos.z + rand.nextFloat() - 0.5f))
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun addBB(e: GetCollisionBoxesEvent1) {
        if (e.entity != null && ::entityBox.isInitialized) {
            if (e.aabb.intersects(entityBoundingBox)) {
                lastBB = getBlockBounds(e.aabb.grow(0.025), e.entity)
                e.collisionBoxesList.addAll(lastBB)
            }
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
        posY += y
        posX += x
        posZ += z
    }

    fun getBlockBounds(bb: AxisAlignedBB, entity: Entity): List<AxisAlignedBB> {
        val list = mutableListOf<AxisAlignedBB>()

        val offX = posX - position.x - 0.5
        val offY = posY - position.y
        val offZ = posZ - position.z - 0.5

        if (!template.init)
            return list

        for (info in template.blocks) {
            val innerList = mutableListOf<AxisAlignedBB>()
            val state = info.block.getStateFromMeta(info.meta)
            info.block.addCollisionBoxToList(state, world, info.pos.add(position.x, position.y, position.z), bb.offset(-offX, -offY, -offZ), innerList, entity, true)
            list.addAll(innerList.map { it.offset(offX, offY, offZ) }.toList())
        }

        return list
    }

    override fun getEntityBoundingBox(): AxisAlignedBB {
        if (!::entityBox.isInitialized) {
            var minX = 0
            var minY = 0
            var minZ = 0
            var maxX = 0
            var maxY = 0
            var maxZ = 0

            for (info in template.blocks) {
                minX = min(info.pos.x, minX)
                minY = min(info.pos.y, minY)
                minZ = min(info.pos.z, minZ)
                maxX = max(info.pos.x, maxX)
                maxY = max(info.pos.y, maxY)
                maxZ = max(info.pos.z, maxZ)
            }

            entityBox = AxisAlignedBB(minX.toDouble() - 1, minY.toDouble(), minZ.toDouble() - 1, maxX.toDouble() + 1, maxY.toDouble() + 1, maxZ.toDouble() + 1)
        }

        return entityBox.offset(posX, posY, posZ)
    }

    protected fun collideWithEntity(entityIn: Entity) {
        val boosted = mutableListOf<Entity>()
        for (bb in getBlockBounds(entityIn.entityBoundingBox, entityIn)) {
            val list = this.world.getEntitiesWithinAABBExcludingEntity(this, bb.grow(1.0))
            for (entity in list.filter { !boosted.contains(it) }) {
                if (entity !is EntityRocket) {

                    boosted.add(entity)
                    if ((lastTickPosY - posY) != 0.0) {
                        entity.motionY = 0.0
                        entity.move(MoverType.SELF, motionX, motionY, motionZ)
                        entity.onGround = true
                    }
                }
            }
        }
    }
}