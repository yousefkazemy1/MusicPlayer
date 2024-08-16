package com.example.croe.solid.srp.good

data class EmployeeData(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val salary: String,
)

val employeeFakeData = EmployeeData(
    id = 1,
    name = "Yousef",
    email = "kazemi@gmail.com",
    phone = "",
    address = "",
    salary = "",
)