package com.mercadolivro.controller.request

import com.mercadolivro.model.CustomerModel
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class PutCustomerRequest(

    @field:NotEmpty(message = "Should pass name")
    var name: String,

    @field:Email(message = "Invalid email")
    var email: String
)