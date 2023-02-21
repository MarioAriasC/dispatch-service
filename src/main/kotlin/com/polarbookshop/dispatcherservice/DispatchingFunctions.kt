package com.polarbookshop.dispatcherservice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux

@Configuration
class DispatchingFunctions {

    @Bean
    fun pack():   (OrderAcceptedMessage) -> Long = { message ->
        logger.info { "The order with id ${message.orderId} is packed" }
        message.orderId
    }

    @Bean
    fun label():  suspend (Flow<Long>) -> Flow<OrderDispatchedMessage> = { orderFlow ->
        orderFlow.map { orderId ->
            logger.info { "The order id $orderId is labeled" }
            OrderDispatchedMessage(orderId)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}