package com.arthur.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SearchEventListener {

    private val log = LoggerFactory.getLogger(SearchEventListener::class.java)

    /**
     * Logs a search query to MongoDB
     * This method can be called by other components when a search is performed
     */
    fun onSearchQuery(guildId: String, query: String) {
        try {
            if (!SharedConfig.isInitialized()) {
                log.warn("Cannot log search - plugin not initialized")
                return
            }
            
            log.debug("Search query from guild $guildId: $query")
            SharedConfig.mongo.logSearch(guildId, query)
            
        } catch (e: Exception) {
            log.error("Failed to log search query", e)
        }
    }
}
