package com.example.croe.solid.srp.good.save

import com.example.croe.solid.srp.good.EmployeeData

interface EmployeeSaver {
    fun saveEmployee()
}

class EmployeeSaverImpl(employeeData: EmployeeData): EmployeeSaver {
    override fun saveEmployee() {

    }

}