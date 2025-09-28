// Model.kt
package app.model

enum class OrderStatus {
    ACCEPTED,
    IN_PROGRESS,
    DONE,
    WAITING_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED
}

data class Item(
    val code: Int,
    val name: String,
    val description: String,
    val price: Double,
    val amount: Int
)

data class Order(
    val code: Int,
    var status: OrderStatus,
    val total: Double,
    val items: MutableList<Item>,
    val hasDiscount: Boolean
)

val items = mutableListOf<Item>()
val orders = mutableListOf<Order>()


fun registerItem(code: Int, name: String, description: String, price: Double, amount: Int) {
    items.add(Item(code, name, description, price, amount))
}

fun updateItem(code: Int, name: String, description: String, price: Double, amount: Int): Boolean {
    val index = items.indexOfFirst { it.code == code }
    return if (index != -1) {
        items[index] = Item(code, name, description, price, amount)
        true
    } else false
}

fun createOrder(code: Int, orderItems: MutableList<Item>, hasDiscount: Boolean): Order {
    var total = orderItems.sumOf { it.price }
    if (hasDiscount) total *= 0.9
    val order = Order(code, OrderStatus.ACCEPTED, total, orderItems, hasDiscount)
    orders.add(order)
    return order
}

fun updateOrderStatus(code: Int, newStatus: OrderStatus): Boolean {
    val order = orders.find { it.code == code }
    return if (order != null) {
        order.status = newStatus
        true
    } else false
}

fun consultOrders(filterStatus: OrderStatus? = null): List<Order> {
    return if (filterStatus == null) orders else orders.filter { it.status == filterStatus }
}

