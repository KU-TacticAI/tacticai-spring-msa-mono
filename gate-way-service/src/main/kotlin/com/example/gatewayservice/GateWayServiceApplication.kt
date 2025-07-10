package com.example.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.gatewayservice", "com.example.commonmodule"])
class GateWayServiceApplication

fun main(args: Array<String>) {
    runApplication<GateWayServiceApplication>(*args)
}
