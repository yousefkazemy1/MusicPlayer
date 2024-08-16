package com.example.croe.solid.srp

import com.example.croe.solid.srp.good.EmployeeFacade
import com.example.croe.solid.srp.good.employeeFakeData

fun main() {
    val employeeFacade = EmployeeFacade(
        employeeFakeData
    )
    employeeFacade.saveEmployee()
}