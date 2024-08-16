package com.example.croe.solid.srp.good.report

import com.example.croe.solid.srp.good.EmployeeData

interface HourReporter {
    fun reportHours()
}

class HourReporterImpl(employeeData: EmployeeData): HourReporter {
    override fun reportHours() {
        val regularHours = calculateRegularHours()
    }
    private fun calculateRegularHours() {

    }
}