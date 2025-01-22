package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
 @MockK
 private lateinit var customerRepository: CustomerRepository

 @MockK
 private lateinit var bookService: BookService

 @MockK
 private lateinit var bCrypt: BCryptPasswordEncoder

 @InjectMockKs
 private lateinit var customerService: CustomerService

 @Test
 fun `should return all customers`() {
  val fakeCustomers = listOf(buildCustomer(), buildCustomer())
  val pageable: Pageable = PageRequest.of(0, 10)
  val page: Page<CustomerModel> = PageImpl(fakeCustomers, pageable, fakeCustomers.size.toLong())

  every { customerRepository.findAll(pageable) } returns page
  val customers = customerService.getAll(null, pageable)

  assertEquals(page, customers)
  verify(exactly = 1) { customerRepository.findAll(pageable)}
  verify(exactly = 0) { customerRepository.findByNameContaining(any(), pageable)}
 }

 @Test
 fun `should return customers when name is passed`() {
  val name = UUID.randomUUID().toString()
  val fakeCustomers = listOf(buildCustomer(), buildCustomer())
  val pageable: Pageable = PageRequest.of(0, 10)
  val page: Page<CustomerModel> = PageImpl(fakeCustomers, pageable, fakeCustomers.size.toLong())


  every { customerRepository.findByNameContaining(name, pageable) } returns page
  val customers = customerService.getAll(name, pageable)

  assertEquals(page, customers)
  verify(exactly = 0) { customerRepository.findAll()}
  verify(exactly = 1) { customerRepository.findByNameContaining(any(), pageable)}
 }

 @Test
 fun `should create customer and ecrypty password`() {
  val initialPassword = Math.random().toString()
  val fakeCustomer = buildCustomer(password = initialPassword)
  val fakePassword = UUID.randomUUID().toString()
  val fakeCustomerEncrypted = fakeCustomer.copy(password = fakePassword)

  every { customerRepository.save(fakeCustomerEncrypted) } returns fakeCustomer
  every { bCrypt.encode(initialPassword) } returns fakePassword

  customerService.createCustomer(fakeCustomer)

  verify(exactly = 1) { customerRepository.save(fakeCustomerEncrypted) }
  verify(exactly = 1) { bCrypt.encode(initialPassword) }
 }

 @Test
 fun `should return customer by id`() {
  val id = Random().nextInt()
  val fakeCustomer = buildCustomer(id = id)

  every { customerRepository.findById(id) } returns Optional.of(fakeCustomer)

  val customer = customerService.getById(id)

  assertEquals(fakeCustomer, customer)
  verify(exactly = 1) { customerRepository.findById(id) }
 }

 @Test
 fun `should return error when customer by id not found`() {
  val id = Random().nextInt()

  every { customerRepository.findById(id) } returns Optional.empty()

  val error = org.junit.jupiter.api.assertThrows<NotFoundException> { customerService.getById(id) }

  assertEquals("Customer [${id}] not exists", error.message)
  assertEquals("ML-201", error.errorCode)
  verify(exactly = 1) { customerRepository.findById(id) }
 }

 @Test
 fun `should update customer`() {
  val id = Random().nextInt()
  val fakeCustomer = buildCustomer(id = id)

  every { customerRepository.existsById(id) } returns true
  every { customerRepository.save(fakeCustomer) } returns fakeCustomer

  customerService.putCustomer(fakeCustomer)

  verify(exactly = 1) { customerRepository.existsById((id)) }
  verify(exactly = 1) { customerRepository.save(fakeCustomer) }
 }

 @Test
 fun `should not update customer`() {
  val id = Random().nextInt()
  val fakeCustomer = buildCustomer(id = id)

  every { customerRepository.existsById(id) } returns false
  every { customerRepository.save(fakeCustomer) } returns fakeCustomer

  val error = org.junit.jupiter.api.assertThrows<NotFoundException> { customerService.putCustomer(fakeCustomer) }

  assertEquals("Customer [${id}] not exists", error.message)
  assertEquals("ML-201", error.errorCode)
  verify(exactly = 1) { customerRepository.existsById(id) }
  verify(exactly = 0) { customerRepository.save(any()) }
 }

 @Test
 fun `should delete customer`() {
  val id = Random().nextInt()
  val fakeCustomer = buildCustomer(id = id)
  val expectedCustomer = fakeCustomer.copy(status = CustomerStatus.INACTIVE)

  every { customerRepository.findById(id) } returns Optional.of(fakeCustomer)
  every { customerRepository.save(fakeCustomer) } returns expectedCustomer
  every { bookService.deleteByCustomer(fakeCustomer) } just runs

  customerService.deleteCustomer(id)

  verify(exactly = 1) { bookService.deleteByCustomer(fakeCustomer) }
  verify(exactly = 1) { customerRepository.save(fakeCustomer) }
 }

 @Test
 fun `should throw not found exception when delete customer`() {
  val id = Random().nextInt()
  val fakeCustomer = buildCustomer(id = id)
  val expectedCustomer = fakeCustomer.copy(status = CustomerStatus.INACTIVE)

  every { customerRepository.findById(id) } returns Optional.empty()
  every { customerRepository.save(fakeCustomer) } returns expectedCustomer
  every { bookService.deleteByCustomer(fakeCustomer) } just runs

  val error = org.junit.jupiter.api.assertThrows<NotFoundException> { customerService.deleteCustomer(id) }

  verify(exactly = 0) { bookService.deleteByCustomer(any()) }
  verify(exactly = 0) { customerRepository.save(any()) }
 }

 @Test
 fun `should return true when email available`() {
  val email = UUID.randomUUID().toString()

  every { customerRepository.existsByEmail(email) } returns false

  val emailAvailable = customerService.emailAvailable(email)

  assertTrue(emailAvailable)
  verify(exactly = 1) { customerRepository.existsByEmail(email) }
 }

 @Test
 fun `should return false when email unavailable`() {
  val email = UUID.randomUUID().toString()

  every { customerRepository.existsByEmail(email) } returns true

  val emailAvailable = customerService.emailAvailable(email)

  assertFalse(emailAvailable)
  verify(exactly = 1) { customerRepository.existsByEmail(email) }
 }

}