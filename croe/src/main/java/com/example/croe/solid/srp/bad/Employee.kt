package com.example.croe.solid.srp.bad

class Employee {

    // this belongs to CFO
    fun calculatePay() {
        val regularHours = calculateRegularHours(160)
    }

    // this belongs to COO
    fun reportHours() {
        val regularHours = calculateRegularHours(170)
    }

    // this belongs to CTO
    fun save() {

    }

    private fun calculateRegularHours(hours: Int): Int {
        val index = 1.5
        return (hours * index).toInt()
    }
}