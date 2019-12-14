package io.github.tiscs.scp.snowflake

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import java.nio.ByteBuffer

// 2019-10-01T10:00:00+08
const val DEFAULT_TWEPOCH = 1569895200000L

class IdWorker(
        private val clusterId: Long,
        private val workerId: Long,
        private val clusterIdBits: Int = 5,
        private val workerIdBits: Int = 5,
        private val sequenceBits: Int = 12,
        private val twepoch: Long = DEFAULT_TWEPOCH
) {
    private val sequenceMask: Long = (-1L shl sequenceBits).inv()

    private var sequence: Long = 0
    private var lastMillis: Long = 0

    init {
        val maxClusterId = (-1L shl clusterIdBits).inv()
        val maxWorkerId = (-1L shl workerIdBits).inv()
        // Preconditions
        require(clusterId in 0..maxClusterId) {
            "Value of \"clusterId\" must be in the range 0 to $maxClusterId."
        }
        require(workerId in 0..maxWorkerId) {
            "Value of \"workerId\" must be in the range 0 to $maxWorkerId."
        }
        require(twepoch > 0) {
            "Value of \"twepoch\" must be greater than 0."
        }
        require(clusterIdBits > 0) {
            "Value of \"clusterIdBits\" must be greater than 0."
        }
        require(workerIdBits > 0) {
            "Value of \"workerIdBits\" must be greater than 0."
        }
        require(sequenceBits > 0) {
            "Value of \"sequenceBits\" must be greater than 0."
        }
        require(clusterIdBits + workerIdBits + sequenceBits < 23) {
            "Sum of \"clusterIdBits\" \"workerIdBits\" and \"sequenceBits\" must be less than 23"
        }
    }

    @Synchronized
    fun nextLong(): Long {
        var timestamp = System.currentTimeMillis()
        if (timestamp == lastMillis) {
            sequence = sequence + 1 and sequenceMask
            if (sequence == 0L) {
                var nextMillis = System.currentTimeMillis()
                while (nextMillis <= lastMillis) {
                    nextMillis = System.currentTimeMillis()
                }
                timestamp = nextMillis
            }
        } else {
            sequence = 0L
        }
        lastMillis = timestamp
        return (timestamp - twepoch shl clusterIdBits + workerIdBits + sequenceBits) or
                (clusterId shl workerIdBits + sequenceBits) or
                (workerId shl sequenceBits) or
                sequence
    }

    fun nextBytes(): ByteArray {
        return ByteBuffer.allocate(Long.SIZE_BYTES).putLong(nextLong()).array()
    }

    fun nextHex(): String {
        return Hex.encodeHexString(nextBytes())
    }

    fun nextBase64(): String {
        return Base64.encodeBase64String(nextBytes())
    }
}