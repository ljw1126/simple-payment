package com.example.simplepayment.controller

import com.example.simplepayment.controller.request.AddBalanceWalletRequest
import com.example.simplepayment.controller.request.CreateWalletRequest
import com.example.simplepayment.controller.response.AddBalanceWalletResponse
import com.example.simplepayment.controller.response.CreateWalletResponse
import com.example.simplepayment.controller.response.SearchWalletResponse
import com.example.simplepayment.service.WalletService
import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [WalletController])
@ActiveProfiles("test")
class WalletControllerTest extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @SpringBean
    WalletService walletService = Mock()

    def "지갑 생성을 요청하면 정상 응답 한다"() {
       given:
       def request = new CreateWalletRequest(1L);
       walletService.create(_) >> new CreateWalletResponse(1L, 1L, BigDecimal.ZERO)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/api/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

       then:
       response.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath('$.id').value(1L))
        .andExpect(jsonPath('$.userId').value(1L))
        .andExpect(jsonPath('$.balance').value(BigDecimal.ZERO))
    }

    def "지갑 조회 요청을 하면 정보를 반환한다"() {
        given:
        def userId = 1L
        def yesterday = LocalDateTime.now().minusDays(1L)
        walletService.findWalletByUserId(_) >> new SearchWalletResponse(1L, userId, BigDecimal.ZERO, yesterday, yesterday)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.get("/api/${userId}/wallet"))

        then:
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.id').value(1L))
                .andExpect(jsonPath('$.userId').value(userId))
                .andExpect(jsonPath('$.balance').value(BigDecimal.ZERO))
                .andExpect(jsonPath('$.createdAt').value(yesterday.toString()))
                .andExpect(jsonPath('$.updatedAt').value(yesterday.toString()))
    }


    def "지갑에 충전 요청을 하면 처리 후 응답한다"() {
        given:
        def userId = 1L
        def now = LocalDateTime.now()
        walletService.addBalance(_) >> new AddBalanceWalletResponse(1L, userId, BigDecimal.valueOf(1000L), now, now)
        def request = new AddBalanceWalletRequest(1L, BigDecimal.valueOf(1000L))

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/wallet/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.id').value(1L))
                .andExpect(jsonPath('$.userId').value(userId))
                .andExpect(jsonPath('$.balance').value(BigDecimal.valueOf(1000L)))
                .andExpect(jsonPath('$.createdAt').value(now.toString()))
                .andExpect(jsonPath('$.updatedAt').value(now.toString()))
    }
}
