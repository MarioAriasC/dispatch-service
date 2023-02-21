package com.polarbookshop.dispatcherservice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.function.Function

@FunctionalSpringBootTest
@Disabled("These tests are only necessary when using the functions alone (no bindings)")
class DispatchingFunctionsIntegrationTests {
    @Autowired
    lateinit var catalog: FunctionCatalog

    @Test
    fun `pack order`() {
        val pack: Function<OrderAcceptedMessage, Long> = catalog.lookup("pack")
        val orderId = 121L
        assertThat(pack(OrderAcceptedMessage(orderId))).isEqualTo(orderId)
    }

    @Test
    fun `label order`() {
        val label: Function<Flux<Long>, Flux<OrderDispatchedMessage>> = catalog.lookup("label")
        val orderId = Flux.just(121L)
        StepVerifier.create(label(orderId))
            .expectNextMatches { dispatchedOrder -> dispatchedOrder == OrderDispatchedMessage(121L) }
            .verifyComplete()
    }

    @Test
    fun `pack and label order`() {
        val packAndLabel: Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>> = catalog.lookup("pack|label")
        val orderId = 121L
        StepVerifier.create(packAndLabel(OrderAcceptedMessage(orderId)))
            .expectNextMatches { dispatchedOrder -> dispatchedOrder == OrderDispatchedMessage(orderId) }
            .verifyComplete()
    }

    private operator fun <T, R> Function<T, R>.invoke(t: T): R = this.apply(t)
}