package com.polarbookshop.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.integration.support.MessageBuilder

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class FunctionsStreamIntegrationTests {
    @Autowired
    lateinit var input: InputDestination

    @Autowired
    lateinit var output: OutputDestination

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun `when order is accepted then should be dispatched`() {
        val orderId = 121L
        val inputMessage = MessageBuilder.withPayload(OrderAcceptedMessage(orderId)).build()
        val expectedOutputMessage = MessageBuilder.withPayload(OrderDispatchedMessage(orderId)).build()
        input.send(inputMessage)

        assertThat(mapper.readValue<OrderDispatchedMessage>(output.receive().payload))
            .isEqualTo(expectedOutputMessage.payload)
    }
}