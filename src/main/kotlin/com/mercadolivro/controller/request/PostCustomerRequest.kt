package com.mercadolivro.controller.request

import com.mercadolivro.model.CustomerModel
import com.mercadolivro.validation.EmailAvailable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class PostCustomerRequest(

    @field:NotEmpty(message = "Name should be passed")
    var name: String,

    @field:Email(message = "Email should be valid")
    @EmailAvailable
    var email: String,

    @field:NotEmpty(message = "Password need to be passed")
    var password: String
)