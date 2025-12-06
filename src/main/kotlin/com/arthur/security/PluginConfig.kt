package com.arthur.security

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "plugins.lavalink-security-plugin")
class PluginConfig {
    
    private val log = LoggerFactory.getLogger(PluginConfig::class.java)
    
    var mongodbUri: String = "mongodb://localhost:27017"
    var database: String = "lavalink_security"
    var collections: Collections = Collections()
    
    class Collections {
        var searches: String = "search_logs"
        var ipBlocks: String = "ip_blocks"
    }
    
    fun initialize() {
        try {
            log.info("Initializing Lavalink Security Plugin...")
            log.info("MongoDB URI: ${mongodbUri.replace(Regex("://.*@"), "://***@")}")
            log.info("Database: $database")
            log.info("Search Collection: ${collections.searches}")
            log.info("IP Block Collection: ${collections.ipBlocks}")
            
            // Validate configuration
            if (mongodbUri.isBlank()) {
                throw IllegalArgumentException("MongoDB URI cannot be blank")
            }
            if (database.isBlank()) {
                throw IllegalArgumentException("Database name cannot be blank")
            }
            
            // Initialize SharedConfig
            SharedConfig.searchCollection = collections.searches
            SharedConfig.ipBlockCollection = collections.ipBlocks
            SharedConfig.mongo = MongoManager(mongodbUri, database)
            
            log.info("Lavalink Security Plugin initialized successfully!")
            
        } catch (e: Exception) {
            log.error("Failed to initialize Lavalink Security Plugin", e)
            throw RuntimeException("Plugin initialization failed", e)
        }
    }
}
