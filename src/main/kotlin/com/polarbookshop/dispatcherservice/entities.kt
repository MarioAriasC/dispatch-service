package com.polarbookshop.dispatcherservice

data class OrderAcceptedMessage(val orderId: Long)
data class OrderDispatchedMessage(val orderId: Long)