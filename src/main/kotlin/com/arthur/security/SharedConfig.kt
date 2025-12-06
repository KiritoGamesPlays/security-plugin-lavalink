package com.arthur.security

object SharedConfig {
    lateinit var mongo: MongoManager
    var searchCollection: String = "search_logs"
    var ipBlockCollection: String = "ip_blocks"
    
    fun isInitialized(): Boolean {
        return ::mongo.isInitialized
    }
}
