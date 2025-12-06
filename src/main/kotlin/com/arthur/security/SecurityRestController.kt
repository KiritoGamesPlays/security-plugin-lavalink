package com.arthur.security

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v4/security")
class SecurityRestController {

    private val log = LoggerFactory.getLogger(SecurityRestController::class.java)

    @GetMapping("/blocked")
    fun getBlockedIps(): ResponseEntity<Map<String, Any>> {
        return try {
            if (!SharedConfig.isInitialized()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf("error" to "Plugin not initialized" as Any))
            }
            
            val blockedIps = SharedConfig.mongo.getAllBlockedIps()
            ResponseEntity.ok(
                mapOf(
                    "success" to true as Any,
                    "count" to blockedIps.size as Any,
                    "ips" to blockedIps as Any
                )
            )
        } catch (e: Exception) {
            log.error("Error getting blocked IPs", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to retrieve blocked IPs" as Any))
        }
    }

    @PostMapping("/block/{ip}")
    fun blockIp(@PathVariable ip: String): ResponseEntity<Map<String, Any>> {
        return try {
            if (!SharedConfig.isInitialized()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf("error" to "Plugin not initialized" as Any))
            }
            
            if (ip.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "IP address cannot be blank" as Any))
            }
            
            val success = SharedConfig.mongo.blockIp(ip)
            
            if (success) {
                log.info("IP blocked via REST API: $ip")
                ResponseEntity.ok(
                    mapOf(
                        "success" to true as Any,
                        "message" to "IP blocked successfully" as Any,
                        "ip" to ip as Any
                    )
                )
            } else {
                ResponseEntity.badRequest()
                    .body(mapOf("error" to "Failed to block IP (invalid format or already blocked)" as Any))
            }
        } catch (e: Exception) {
            log.error("Error blocking IP: $ip", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to block IP" as Any))
        }
    }

    @DeleteMapping("/block/{ip}")
    @PostMapping("/unblock/{ip}")
    fun unblockIp(@PathVariable ip: String): ResponseEntity<Map<String, Any>> {
        return try {
            if (!SharedConfig.isInitialized()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf("error" to "Plugin not initialized" as Any))
            }
            
            if (ip.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "IP address cannot be blank" as Any))
            }
            
            val success = SharedConfig.mongo.unblockIp(ip)
            
            if (success) {
                log.info("IP unblocked via REST API: $ip")
                ResponseEntity.ok(
                    mapOf(
                        "success" to true as Any,
                        "message" to "IP unblocked successfully" as Any,
                        "ip" to ip as Any
                    )
                )
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "IP not found in block list" as Any))
            }
        } catch (e: Exception) {
            log.error("Error unblocking IP: $ip", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to unblock IP" as Any))
        }
    }

    @GetMapping("/check/{ip}")
    fun checkIp(@PathVariable ip: String): ResponseEntity<Map<String, Any>> {
        return try {
            if (!SharedConfig.isInitialized()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf("error" to "Plugin not initialized" as Any))
            }
            
            if (ip.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "IP address cannot be blank" as Any))
            }
            
            val isBlocked = SharedConfig.mongo.isBlocked(ip)
            
            ResponseEntity.ok(
                mapOf(
                    "success" to true as Any,
                    "ip" to ip as Any,
                    "blocked" to isBlocked as Any
                )
            )
        } catch (e: Exception) {
            log.error("Error checking IP: $ip", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to check IP" as Any))
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        return try {
            val initialized = SharedConfig.isInitialized()
            
            if (initialized) {
                ResponseEntity.ok(
                    mapOf(
                        "status" to "healthy" as Any,
                        "plugin" to "lavalink-security-plugin" as Any,
                        "version" to "1.0.0" as Any,
                        "mongodb" to "connected" as Any
                    )
                )
            } else {
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf(
                        "status" to "unhealthy" as Any,
                        "error" to "Plugin not initialized" as Any
                    ))
            }
        } catch (e: Exception) {
            log.error("Health check failed", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf(
                    "status" to "unhealthy" as Any,
                    "error" to (e.message ?: "Unknown error") as Any
                ))
        }
    }
}
