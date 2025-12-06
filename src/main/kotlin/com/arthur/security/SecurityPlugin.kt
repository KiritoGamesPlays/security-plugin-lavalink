package com.arthur.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SecurityPlugin {

    private val log = LoggerFactory.getLogger(SecurityPlugin::class.java)

    @Autowired
    private lateinit var config: PluginConfig

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        try {
            log.info("Lavalink Security Plugin starting...")
            config.initialize()
            log.info("Lavalink Security Plugin started successfully!")
        } catch (e: Exception) {
            log.error("Failed to start Lavalink Security Plugin", e)
        }
    }
}
