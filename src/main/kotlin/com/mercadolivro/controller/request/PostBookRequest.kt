package com.mercadolivro.controller.request

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class PostBookRequest(
    @field:NotEmpty(message = "Name should be passed")
    var name: String,
    @field:NotNull(message = "Price should be passed")
    var price: BigDecimal,
    @JsonAlias("customer_id")
    var customerId: Int
)