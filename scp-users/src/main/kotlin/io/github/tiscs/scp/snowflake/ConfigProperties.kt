package io.github.tiscs.scp.snowflake

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("snowflake")
class ConfigProperties {
    var clusterId: Long = 0
    var workerId: Long = 0
    var workerSequence: String? = null
    var clusterIdBits: Int = 5
    var workerIdBits: Int = 5
    var sequenceBits: Int = 12
    var idEpoch = DEFAULT_ID_EPOCH
}
