package com.mercadolivro.controller.request

import com.mercadolivro.model.CustomerModel
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PutBookRequest(
    @field:NotEmpty(message = "Name should be passed")
    var name: String?,

    @field:NotNull(message = "Price should be passed")
    var price: BigDecimal?
)