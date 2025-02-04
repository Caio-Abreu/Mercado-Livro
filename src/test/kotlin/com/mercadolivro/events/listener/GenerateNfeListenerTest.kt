package com.mercadolivro.events.listener

import com.mercadolivro.events.PurchaseEvent
import com.mercadolivro.helper.buildPurchase
import com.mercadolivro.service.PurchaseService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GenerateNfeListenerTest {
 @MockK
 private lateinit var purchaseService: PurchaseService

 @InjectMockKs
 private lateinit var generateNfeListener: GenerateNfeListener

 @BeforeEach
 fun setUp() {
  MockKAnnotations.init(this)
 }

 @Test
 fun `should generate nfe`() {
  val purchase = buildPurchase(nfe = null)
  val fakeNfe = UUID.randomUUID()
  val purchaseExpected = purchase.copy(nfe = fakeNfe.toString())
  mockkStatic(UUID::class)

  every { UUID.randomUUID() } returns fakeNfe
  every { purchaseService.update(purchaseExpected) } just runs

  generateNfeListener.listen(PurchaseEvent(this, purchase))

  verify { purchaseService.update(purchaseExpected) }
 }

}