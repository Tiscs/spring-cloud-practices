package io.github.tiscs.scp.models

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.v3.oas.annotations.media.Schema
import java.net.URI

/**
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807: Problem Details for HTTP APIs</a>.
 */
@JsonSerialize(using = ProblemDetailsSerializer::class)
class ProblemDetails(
    @Schema(example = "https://tools.ietf.org/html/rfc2616#section-10.4.1")
    val type: URI?,
    @Schema(example = "Bad Request")
    val title: String?,
    @Schema(example = "400")
    val status: Int?,
    @Schema(example = "The request could not be understood by the server due to malformed syntax.")
    val detail: String?,
    @Schema(example = "null")
    val instance: URI?,
) : LinkedHashMap<String, Any?>() {
    companion object {
        fun builder(): ProblemDetailsBuilder = ProblemDetailsBuilder()
    }

    @Schema(hidden = true)
    override fun isEmpty(): Boolean {
        return super.isEmpty()
    }
}

@Suppress("unused")
class ProblemDetailsBuilder(
    private var type: URI? = null,
    private var title: String? = null,
    private var status: Int? = null,
    private var detail: String? = null,
    private var instance: URI? = null,
    private var extensions: LinkedHashMap<String, Any?> = LinkedHashMap(),
) {
    fun build(): ProblemDetails = ProblemDetails(type, title, status, detail, instance).also {
        it.putAll(extensions)
    }

    fun type(type: URI?): ProblemDetailsBuilder {
        this.type = type
        return this
    }

    fun type(type: String?): ProblemDetailsBuilder {
        this.type = if (type == null || type.isEmpty()) {
            null
        } else {
            URI.create(type)
        }
        return this
    }

    fun title(title: String?): ProblemDetailsBuilder {
        this.title = title
        return this
    }

    fun status(status: Int?): ProblemDetailsBuilder {
        this.status = status
        return this
    }

    fun detail(detail: String?): ProblemDetailsBuilder {
        this.detail = detail
        return this
    }

    fun instance(instance: URI?): ProblemDetailsBuilder {
        this.instance = instance
        return this
    }

    fun instance(instance: String?): ProblemDetailsBuilder {
        this.instance = if (instance == null || instance.isEmpty()) {
            null
        } else {
            URI.create(instance)
        }
        return this
    }

    fun extension(key: String, value: Any?): ProblemDetailsBuilder {
        this.extensions[key] = value
        return this
    }

    fun extensions(extensions: Map<String, Any?>): ProblemDetailsBuilder {
        this.extensions.putAll(extensions)
        return this
    }

    fun extensions(vararg extensions: Pair<String, Any?>): ProblemDetailsBuilder {
        this.extensions.putAll(extensions)
        return this
    }
}

class ProblemDetailsSerializer : JsonSerializer<ProblemDetails>() {
    override fun serialize(details: ProblemDetails, json: JsonGenerator, provider: SerializerProvider) {
        json.writeStartObject()
        if (details.type != null) {
            json.writeStringField("type", details.type.toString())
        }
        if (details.title != null) {
            json.writeStringField("title", details.title)
        }
        if (details.status != null) {
            json.writeNumberField("status", details.status)
        }
        if (details.detail != null) {
            json.writeStringField("detail", details.detail)
        }
        if (details.instance != null) {
            json.writeStringField("instance", details.instance.toString())
        }
        for ((key, value) in details) {
            if (key.isNotEmpty() && value != null) {
                json.writeObjectField(key, value)
            }
        }
    }
}
