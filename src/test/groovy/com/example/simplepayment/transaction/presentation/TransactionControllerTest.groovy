package com.example.simplepayment.transaction.presentation

import com.example.simplepayment.transaction.application.TransactionService
import com.example.simplepayment.transaction.presentation.request.ChargeTransactionRequest
import com.example.simplepayment.transaction.presentation.request.PaymentTransactionRequest
import com.example.simplepayment.transaction.presentation.response.ChargeTransactionResponse
import com.example.simplepayment.transaction.presentation.response.PaymentTransactionResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [TransactionController])
@ActiveProfiles("test")
class TransactionControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @SpringBean
    private TransactionService transactionService = Mock()

    def "충전 요청 성공한다" () {
        given:
        def userId = 1L
        def orderId = 1L
        def amount = BigDecimal.TEN
        def request = new ChargeTransactionRequest(userId, orderId, amount)

        transactionService.charge(request) >> new ChargeTransactionResponse(1L, amount);

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.walletId').value(1L))
                .andExpect(jsonPath('$.balance').value(amount))
    }

    def "결제 요청 성공한다" () {
        given:
        def walletId = 1L
        def courseId = 99L
        def amount = BigDecimal.valueOf(10000L)

        def request = new PaymentTransactionRequest(walletId, courseId, amount)

        transactionService.payment(request) >> new PaymentTransactionResponse(walletId, BigDecimal.ZERO)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.walletId').value(walletId))
                .andExpect(jsonPath('$.balance').value(BigDecimal.ZERO))
    }

}
