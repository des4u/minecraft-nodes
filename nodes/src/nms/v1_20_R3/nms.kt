/**
 * NMS 1.20.4 type aliases
 * https://nms.screamingsandals.org/1.20.4/
 * 
 * NOTE: for 1.20.4, use the "Mojang" names for classes and methods.
 */

package phonon.nodes.nms

import java.util.Optional
import net.minecraft.core.BlockPos as NMSBlockPos
import net.minecraft.world.entity.player.Player as NMSPlayer
import net.minecraft.world.level.block.state.BlockState as NMSBlockState
import net.minecraft.world.level.chunk.LevelChunk as NMSChunk
import net.minecraft.network.chat.Component
import net.minecraft.network.PacketListener as NMSPacketListener
import net.minecraft.network.protocol.Packet as NMSPacket
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket as NMSPacketLevelChunkWithLightPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket as NMSPacketSetEntityData
import net.minecraft.network.syncher.EntityDataSerializers as NMSEntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData as NMSSynchedEntityData
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

// re-exported type aliases
internal typealias NMSBlockPos = NMSBlockPos
internal typealias NMSBlockState = NMSBlockState
internal typealias NMSChunk = NMSChunk
internal typealias NMSPlayer = NMSPlayer
internal typealias NMSPacketLevelChunkWithLightPacket = NMSPacketLevelChunkWithLightPacket
internal typealias NMSPacketSetEntityData = NMSPacketSetEntityData
internal typealias CraftWorld = CraftWorld
internal typealias CraftPlayer = CraftPlayer
internal typealias CraftMagicNumbers = CraftMagicNumbers

/**
 * Wrapper for getting Bukkit player connection and sending packet.
 * Player connection field and sendPacket name differ between versions.
 */
internal fun <T: NMSPacketListener> Player.sendPacket(p: NMSPacket<T>) {
    return (this as CraftPlayer).handle.connection.send(p)
}

/**
 * Create custom name packet for armor stand entity.
 * Updated for 1.20.4 with Component.literal and new DataValue API
 */
public fun ArmorStand.createArmorStandNamePacket(name: String): NMSPacketSetEntityData {
    val entityId = (this as CraftEntity).handle.id
    val nameComponent = Optional.of(Component.literal(name) as Component)
    
    val dataItems: MutableList<NMSSynchedEntityData.DataValue<*>> = ArrayList()
    dataItems.add(NMSSynchedEntityData.DataValue(0, NMSEntityDataSerializers.BYTE, 0x20.toByte())) // invisible
    dataItems.add(NMSSynchedEntityData.DataValue(2, NMSEntityDataSerializers.OPTIONAL_COMPONENT, nameComponent))
    dataItems.add(NMSSynchedEntityData.DataValue(3, NMSEntityDataSerializers.BOOLEAN, true))
    dataItems.add(NMSSynchedEntityData.DataValue(5, NMSEntityDataSerializers.BOOLEAN, true))

    return NMSPacketSetEntityData(entityId, dataItems)
}

public class SynchedEntityDataWrapper(
    private val dataItems: List<NMSSynchedEntityData.DataValue<Any>>,
): NMSSynchedEntityData(null) {
    override fun packDirty(): List<NMSSynchedEntityData.DataValue<Any>>? {
        return this.dataItems
    }
}
