package com.cartvia.app.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RoleSerializer::class)
enum class Role {
    CUSTOMER, STORE_STAFF, ADMIN, UNKNOWN
}

@Serializable(with = SessionStatusSerializer::class)
enum class SessionStatus {
    ACTIVE, CHECKED_OUT, ABANDONED, UNKNOWN
}

@Serializable(with = OrderStatusSerializer::class)
enum class OrderStatus {
    PENDING, PAID, FAILED, CANCELLED, UNKNOWN
}

@Serializable(with = PaymentStatusSerializer::class)
enum class PaymentStatus {
    INITIATED, SUCCESS, FAILED, UNKNOWN
}

@Serializable(with = VerificationStatusSerializer::class)
enum class VerificationStatus {
    PENDING, VERIFIED, MISMATCH, UNKNOWN
}

@Serializable(with = CartSourceSerializer::class)
enum class CartSource {
    SCAN, MANUAL, UNKNOWN
}

@Serializable(with = TrolleyStatusSerializer::class)
enum class TrolleyStatus {
    INACTIVE, ACTIVE, IN_SESSION, MAINTENANCE, UNKNOWN
}

@Serializable(with = WebSocketEventTypeSerializer::class)
enum class WebSocketEventType {
    TROLLEY_CONNECTED, TROLLEY_DISCONNECTED, PRODUCT_SCANNED,
    PRODUCT_ADDED, PRODUCT_REMOVED, CART_UPDATED, WEIGHT_UPDATED,
    WEIGHT_VALIDATION_RESULT, CHECKOUT_STARTED, PAYMENT_COMPLETED,
    SESSION_UPDATED, UNKNOWN
}

// Generic Serializer for Safe Enum Decoding
abstract class SafeEnumSerializer<T : Enum<T>>(
    val enumValues: Array<T>,
    val unknownValue: T
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SafeEnum", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.name)
    override fun deserialize(decoder: Decoder): T {
        val name = decoder.decodeString()
        return enumValues.find { it.name == name } ?: unknownValue
    }
}

object RoleSerializer : SafeEnumSerializer<Role>(Role.values(), Role.UNKNOWN)
object SessionStatusSerializer : SafeEnumSerializer<SessionStatus>(SessionStatus.values(), SessionStatus.UNKNOWN)
object OrderStatusSerializer : SafeEnumSerializer<OrderStatus>(OrderStatus.values(), OrderStatus.UNKNOWN)
object PaymentStatusSerializer : SafeEnumSerializer<PaymentStatus>(PaymentStatus.values(), PaymentStatus.UNKNOWN)
object VerificationStatusSerializer : SafeEnumSerializer<VerificationStatus>(VerificationStatus.values(), VerificationStatus.UNKNOWN)
object CartSourceSerializer : SafeEnumSerializer<CartSource>(CartSource.values(), CartSource.UNKNOWN)
object TrolleyStatusSerializer : SafeEnumSerializer<TrolleyStatus>(TrolleyStatus.values(), TrolleyStatus.UNKNOWN)
object WebSocketEventTypeSerializer : SafeEnumSerializer<WebSocketEventType>(WebSocketEventType.values(), WebSocketEventType.UNKNOWN)
