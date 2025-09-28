package app.view// Model.kt
import app.model.*

//auxiliary functions
fun readPrice(prompt: String): Double {
    var price: Double
    do {
        print(prompt)
        val input = readln().replace(",", ".")
        price = input.toDoubleOrNull() ?: -1.0
        if (price <= 0.0) println("Invalid value. Enter a price greater than zero.")
    } while (price <= 0.0)
    return price
}

fun readAmount(prompt: String): Int {
    var amount: Int
    do {
        print(prompt)
        amount = readlnOrNull()?.toIntOrNull() ?: -1
        if (amount < 0) println("Invalid value. Enter a non-negative integer.")
    } while (amount < 0)
    return amount
}

fun readOption(prompt: String, validRange: IntRange): Int {
    var option: Int
    do {
        print(prompt)
        option = readlnOrNull()?.toIntOrNull() ?: -1
        if (option !in validRange) println("Invalid option. Choose between ${validRange.first} and ${validRange.last}.")
    } while (option !in validRange)
    return option
}

fun readExistingCode(prompt: String, validCodes: List<Int>): Int {
    var code: Int
    do {
        print(prompt)
        code = readlnOrNull()?.toIntOrNull() ?: -1
        if (code !in validCodes) println("Invalid code. Try again.")
    } while (code !in validCodes)
    return code
}

fun readYesNo(prompt: String): Boolean {
    var input: String
    do {
        print(prompt)
        input = readln().trim().uppercase()
        if (input !in listOf("Y", "N")) println("Invalid input. Enter Y or N.")
    } while (input !in listOf("Y", "N"))
    return input == "Y"
}

fun main() {
    var itemCodeGenerator = 0
    var orderCodeGenerator = 0
    var option: Int

    do {
        println("==================================================")
        println("                    MENU                          ")
        println("1. Register Item")
        println("2. Update Item")
        println("3. Create Order")
        println("4. Update Order")
        println("5. Consult Orders")
        println("0. Exit")
        println("==================================================")

        option = readOption("Choose an option: ", 0..5)

        when (option) {
            1 -> {
                do {
                    print("Product name: ")
                    val name = readln()
                    print("Description: ")
                    val description = readln()
                    val price = readPrice("Price: ")
                    val amount = readAmount("Stock quantity: ")

                    itemCodeGenerator++
                    registerItem(itemCodeGenerator, name, description, price, amount)
                    println("Item registered successfully, code: $itemCodeGenerator")
                } while (readYesNo("Register another item? (Y/N): "))
            }

            2 -> {
                if (items.isEmpty()) {
                    println("No items registered.")
                } else {
                    items.forEach {
                        println("Code: ${it.code}, Name: ${it.name}, Price: ${"%.2f".format(it.price)}, " +
                                "Stock: ${it.amount}")
                    }
                    val code = readExistingCode("Enter the item code to update: ",
                        items.map { it.code })

                    print("New name: ")
                    val name = readln()
                    print("New description: ")
                    val description = readln()
                    val price = readPrice("New price: ")
                    val amount = readAmount("New stock quantity: ")

                    if (updateItem(code, name, description, price, amount)) {
                        println("Item updated successfully.")
                    } else {
                        println("Update failed. Item not found.")
                    }
                }
            }

            3 -> {
                if (items.isEmpty()) {
                    println("No items registered. Register items first.")
                } else {
                    val orderItems = mutableListOf<Item>()
                    do {
                        items.forEach {
                            println("Code: ${it.code}, Name: ${it.name}, Price: ${"%.2f".format(it.price)}," +
                                    " Stock: ${it.amount}")
                        }
                        val code = readExistingCode("Enter the item code to add: ",
                            items.map { it.code })
                        val item = items.find { it.code == code }
                        if (item != null) {
                            orderItems.add(item)
                            println("Added: ${item.name} (${ "%.2f".format(item.price) })")
                        }
                    } while (readYesNo("Add another item? (Y/N): "))

                    if (orderItems.isNotEmpty()) {
                        val hasDiscount = readYesNo("Apply discount coupon? (Y/N): ")
                        orderCodeGenerator++
                        val order = createOrder(orderCodeGenerator, orderItems, hasDiscount)
                        println("Order created successfully, code: ${order.code}," +
                                " total: ${"%.2f".format(order.total)}")
                    } else {
                        println("Order not created. No items selected.")
                    }
                }
            }

            4 -> {
                if (orders.isEmpty()) {
                    println("No orders registered.")
                } else {
                    orders.forEach {
                        println("Code: ${it.code}, Status: ${it.status}, Total: ${"%.2f".format(it.total)}")
                    }
                    val code = readExistingCode("Enter the order code to update: ",
                        orders.map { it.code })

                    println("Choose new status:")
                    OrderStatus.entries.forEachIndexed { index, status ->
                        println("${index + 1} - $status")
                    }
                    val statusOption = readOption("Option: ", 1..OrderStatus.entries.size)
                    val newStatus = OrderStatus.entries[statusOption - 1]

                    if (updateOrderStatus(code, newStatus)) {
                        println("Order updated successfully.")
                    } else {
                        println("Update failed. Order not found.")
                    }
                }
            }

            5 -> {
                if (orders.isEmpty()) {
                    println("No orders registered.")
                } else {
                    println("Filter orders by status:")
                    println("0 - ALL")
                    OrderStatus.entries.forEachIndexed { index, status ->
                        println("${index + 1} - $status")
                    }
                    val filterOption = readOption("Option: ", 0..OrderStatus.entries.size)
                    val filterStatus = if (filterOption == 0) null else OrderStatus.entries[filterOption - 1]

                    val filteredOrders = consultOrders(filterStatus)
                    if (filteredOrders.isEmpty()) {
                        println("No orders found with this filter.")
                    } else {
                        filteredOrders.forEach { order ->
                            println("Code: ${order.code}, Status: ${order.status}," +
                                    " Total: ${"%.2f".format(order.total)}," +
                                    " Discount: ${if (order.hasDiscount) "10%" else "0%"}")
                            order.items.forEach { item ->
                                println("   Item: ${item.name}, Price: ${"%.2f".format(item.price)}")
                            }
                        }
                    }
                }
            }

            0 -> println("System shutting down...")
            else -> println("Invalid option.")
        }
    } while (option != 0)
}