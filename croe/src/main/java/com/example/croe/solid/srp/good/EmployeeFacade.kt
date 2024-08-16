package com.example.croe.solid.srp.good

import com.example.croe.solid.srp.good.pay.PayCalculatorImpl
import com.example.croe.solid.srp.good.report.HourReporterImpl
import com.example.croe.solid.srp.good.save.EmployeeSaverImpl

class EmployeeFacade(
    employeeData: EmployeeData
) {
    private val payCalculator = PayCalculatorImpl(employeeData)
    private val hourReporter = HourReporterImpl(employeeData)
    private val employeeSaver = EmployeeSaverImpl(employeeData)
    fun calculatePay() {
        payCalculator.calculatePay()
    }

    fun reportHours() {
        hourReporter.reportHours()
    }

    fun saveEmployee() {
        employeeSaver.saveEmployee()
    }
}