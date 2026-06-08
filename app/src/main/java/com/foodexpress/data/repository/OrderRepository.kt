package com.foodexpress.data.repository

import com.foodexpress.core.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface OrderRepository {
    suspend fun placeOrder(order: Order): Result<String>
    suspend fun getOrders(userId: String): Result<List<Order>>
    fun observeOrders(userId: String): Flow<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order>
    fun observeOrder(orderId: String): Flow<Order?>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
    suspend fun getMerchantOrders(restaurantId: String): Result<List<Order>>
}

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : OrderRepository {

    override suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val data = mapOf(
                "customerId" to order.customerId,
                "customerName" to order.customerName,
                "restaurantId" to order.restaurantId,
                "restaurantName" to order.restaurantName,
                "items" to order.items.map { item ->
                    mapOf(
                        "menuItemId" to item.menuItem.id,
                        "name" to item.menuItem.name,
                        "price" to item.menuItem.price,
                        "quantity" to item.quantity,
                        "note" to item.note
                    )
                },
                "totalAmount" to order.totalAmount,
                "deliveryFee" to order.deliveryFee,
                "status" to OrderStatus.PLACED.name,
                "deliveryAddress" to mapOf(
                    "label" to order.deliveryAddress.label,
                    "fullAddress" to order.deliveryAddress.fullAddress,
                    "contactPhone" to order.deliveryAddress.contactPhone
                ),
                "contactPhone" to order.contactPhone,
                "note" to order.note,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            val ref = firestore.collection("orders").add(data).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("customerId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val orders = snapshot.documents.mapNotNull { doc ->
                parseOrder(doc.id, doc.data ?: emptyMap())
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("customerId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    parseOrder(doc.id, doc.data ?: emptyMap())
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc = firestore.collection("orders").document(orderId).get().await()
            val order = parseOrder(doc.id, doc.data ?: emptyMap())
                ?: throw Exception("Order not found")
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeOrder(orderId: String): Flow<Order?> = callbackFlow {
        val listener = firestore.collection("orders").document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val order = snapshot?.let {
                    parseOrder(it.id, it.data ?: emptyMap())
                }
                trySend(order)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            firestore.collection("orders").document(orderId).update(
                mapOf(
                    "status" to status.name,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMerchantOrders(restaurantId: String): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("restaurantId", restaurantId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val orders = snapshot.documents.mapNotNull { doc ->
                parseOrder(doc.id, doc.data ?: emptyMap())
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseOrder(id: String, data: Map<String, Any>): Order? {
        return try {
            Order(
                id = id,
                customerId = data["customerId"] as? String ?: "",
                customerName = data["customerName"] as? String ?: "",
                restaurantId = data["restaurantId"] as? String ?: "",
                restaurantName = data["restaurantName"] as? String ?: "",
                totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                deliveryFee = (data["deliveryFee"] as? Number)?.toDouble() ?: 0.0,
                status = try {
                    OrderStatus.valueOf(data["status"] as? String ?: "PLACED")
                } catch (e: Exception) {
                    OrderStatus.PLACED
                },
                note = data["note"] as? String ?: "",
                contactPhone = data["contactPhone"] as? String ?: "",
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
            )
        } catch (e: Exception) {
            null
        }
    }
}
