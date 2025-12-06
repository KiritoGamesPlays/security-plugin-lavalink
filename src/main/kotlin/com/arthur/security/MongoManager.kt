package com.arthur.security

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.slf4j.LoggerFactory

class MongoManager(uri: String, databaseName: String) {

    private val log = LoggerFactory.getLogger(MongoManager::class.java)
    val client: MongoClient
    val database: MongoDatabase

    init {
        try {
            client = MongoClients.create(uri)
            database = client.getDatabase(databaseName)
            
            // Test connection
            database.runCommand(Document("ping", 1))
            log.info("MongoDB connection established successfully")
        } catch (e: Exception) {
            log.error("Failed to connect to MongoDB", e)
            throw RuntimeException("MongoDB connection failed: ${e.message}", e)
        }
    }

    fun getCollection(name: String): MongoCollection<Document> {
        return try {
            database.getCollection(name)
        } catch (e: Exception) {
            log.error("Failed to get collection: $name", e)
            throw RuntimeException("Failed to get collection: $name", e)
        }
    }

    fun logSearch(guildId: String, query: String) {
        try {
            val sanitizedQuery = query.take(1000) // Limit query length
            getCollection(SharedConfig.searchCollection).insertOne(
                Document()
                    .append("guildId", guildId)
                    .append("query", sanitizedQuery)
                    .append("timestamp", System.currentTimeMillis())
            )
            log.debug("Search logged for guild: $guildId")
        } catch (e: Exception) {
            log.error("Failed to log search for guild: $guildId", e)
        }
    }

    fun blockIp(ip: String): Boolean {
        return try {
            if (!isValidIp(ip)) {
                log.warn("Invalid IP format: $ip")
                return false
            }
            
            // Check if already blocked
            if (isBlocked(ip)) {
                log.info("IP already blocked: $ip")
                return true
            }
            
            getCollection(SharedConfig.ipBlockCollection).insertOne(
                Document()
                    .append("ip", ip)
                    .append("blockedAt", System.currentTimeMillis())
            )
            log.info("IP blocked successfully: $ip")
            true
        } catch (e: Exception) {
            log.error("Failed to block IP: $ip", e)
            false
        }
    }

    fun unblockIp(ip: String): Boolean {
        return try {
            val result = getCollection(SharedConfig.ipBlockCollection).deleteOne(
                Document("ip", ip)
            )
            
            if (result.deletedCount > 0) {
                log.info("IP unblocked successfully: $ip")
                true
            } else {
                log.warn("IP not found in block list: $ip")
                false
            }
        } catch (e: Exception) {
            log.error("Failed to unblock IP: $ip", e)
            false
        }
    }

    fun isBlocked(ip: String): Boolean {
        return try {
            getCollection(SharedConfig.ipBlockCollection)
                .find(Document("ip", ip))
                .first() != null
        } catch (e: Exception) {
            log.error("Failed to check if IP is blocked: $ip", e)
            false
        }
    }
    
    fun getAllBlockedIps(): List<String> {
        return try {
            getCollection(SharedConfig.ipBlockCollection)
                .find()
                .map { it.getString("ip") }
                .filterNotNull()
                .toList()
        } catch (e: Exception) {
            log.error("Failed to get blocked IPs list", e)
            emptyList()
        }
    }

    private fun isValidIp(ip: String): Boolean {
        // Basic IPv4 validation
        val ipv4Pattern = Regex(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        
        // Basic IPv6 validation (simplified)
        val ipv6Pattern = Regex(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::1$|^::(ffff:)?((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern)
    }

    fun close() {
        try {
            client.close()
            log.info("MongoDB connection closed")
        } catch (e: Exception) {
            log.error("Error closing MongoDB connection", e)
        }
    }
}
