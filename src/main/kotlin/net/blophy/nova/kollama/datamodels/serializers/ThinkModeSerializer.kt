package net.blophy.nova.kollama.datamodels.serializers

import net.blophy.nova.kollama.datamodels.ThinkMode
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object ThinkModeSerializer : KSerializer<ThinkMode> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("datamodels.serializers.ThinkMode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ThinkMode) {
        when (value) {
            ThinkMode.ON -> encoder.encodeBoolean(true)
            ThinkMode.OFF -> encoder.encodeBoolean(false)
            ThinkMode.LOW -> encoder.encodeString("low")
            ThinkMode.MEDIUM -> encoder.encodeString("medium")
            ThinkMode.HIGH -> encoder.encodeString("high")
        }
    }

    override fun deserialize(decoder: Decoder): ThinkMode {
        return try {
            val bool = decoder.decodeBoolean()
            if (bool) ThinkMode.ON else ThinkMode.OFF
        } catch (_: SerializationException) {
            when (val str = decoder.decodeString()) {
                "low" -> ThinkMode.LOW
                "medium" -> ThinkMode.MEDIUM
                "high" -> ThinkMode.HIGH
                else -> throw SerializationException("Unknown value: $str")
            }
        }
    }
}

