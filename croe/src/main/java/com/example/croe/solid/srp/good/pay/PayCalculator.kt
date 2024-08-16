package com.example.croe.solid.srp.good.pay

import com.example.croe.solid.srp.good.EmployeeData

interface PayCalculator {
    fun calculatePay()
}

class PayCalculatorImpl(
    private val employeeData: EmployeeData
) : PayCalculator {
    override fun calculatePay() {
        val regularHours = calculateRegularHours()
    }

    private fun calculateRegularHours() {

    }
}