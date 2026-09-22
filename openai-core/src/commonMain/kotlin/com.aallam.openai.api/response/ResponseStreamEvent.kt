package com.aallam.openai.api.response

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

/**
 * Represents an event emitted while a [Response] is streamed.
 *
 * The event payload is kept as a [JsonObject] so that newly introduced event types and fields
 * remain accessible without a client release. Use [type] to branch on the event kind and the
 * convenience accessors (or [json] directly) to read the payload.
 *
 * @property type the type of the event, or [ResponseStreamEventType.UNKNOWN] if unrecognized.
 * @property json the raw payload of the event.
 */
public data class ResponseStreamEvent(
    public val type: ResponseStreamEventType,
    public val json: JsonObject,
) {

    /**
     * The raw JSON string of [json].
     */
    public val raw: String get() = json.toString()

    /**
     * The identifier of the response this event belongs to.
     */
    public val responseId: String? get() = json.string("response_id") ?: (json["response"] as? JsonObject)?.string("id")

    /**
     * The sequence number of this event, monotonically increasing within a response.
     */
    public val sequenceNumber: Long? get() = (json["sequence_number"] as? JsonPrimitive)?.longOrNull

    /**
     * The identifier of the output item this event belongs to.
     */
    public val itemId: String? get() = json.string("item_id")

    /**
     * The index of the output item this event belongs to.
     */
    public val outputIndex: Int? get() = (json["output_index"] as? JsonPrimitive)?.intOrNull

    /**
     * The index of the content part this event belongs to.
     */
    public val contentIndex: Int? get() = (json["content_index"] as? JsonPrimitive)?.intOrNull

    /**
     * The index of the summary part this event belongs to.
     */
    public val summaryIndex: Int? get() = (json["summary_index"] as? JsonPrimitive)?.intOrNull

    /**
     * The incremental text carried by this event, if any.
     *
     * Populated for delta events such as [ResponseStreamEventType.RESPONSE_OUTPUT_TEXT_DELTA],
     * [ResponseStreamEventType.RESPONSE_REFUSAL_DELTA] and
     * [ResponseStreamEventType.RESPONSE_REASONING_SUMMARY_TEXT_DELTA].
     */
    public val delta: String? get() = json.string("delta")

    /**
     * The full text carried by this event, if any.
     *
     * Populated for done events such as [ResponseStreamEventType.RESPONSE_OUTPUT_TEXT_DONE].
     */
    public val text: String? get() = json.string("text")

    /**
     * The message describing the failure, if any.
     *
     * Populated for [ResponseStreamEventType.ERROR] and [ResponseStreamEventType.RESPONSE_FAILED].
     */
    public val message: String?
        get() = json.string("message")
            ?: (((json["response"] as? JsonObject)?.get("error")) as? JsonObject)?.string("message")

    /**
     * The identifier of the output item that was added or completed, if any.
     */
    public val id: String? get() = (json["item"] as? JsonObject)?.string("id") ?: json.string("id")

    public companion object {

        /**
         * Creates a [ResponseStreamEvent] from its raw [json] payload.
         *
         * The event type is read from the payload's `type` field, and falls back to
         * [ResponseStreamEventType.UNKNOWN] when that field is missing, is not a string, or holds an
         * unrecognized value.
         */
        public fun of(json: JsonObject): ResponseStreamEvent {
            val type = (json["type"] as? JsonPrimitive)?.contentOrNull
            return ResponseStreamEvent(
                type = ResponseStreamEventType.fromEvent(type.orEmpty()),
                json = json,
            )
        }
    }
}

private fun JsonObject.string(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull

/**
 * Represents an event type emitted while a [Response] is streamed.
 *
 * @property event the string representation of the event type.
 */
public enum class ResponseStreamEventType(public val event: String) {

    /** Occurs when a Response is created. */
    RESPONSE_CREATED("response.created"),

    /** Occurs when a Response is queued. */
    RESPONSE_QUEUED("response.queued"),

    /** Occurs when a Response moves to an in_progress status. */
    RESPONSE_IN_PROGRESS("response.in_progress"),

    /** Occurs when a Response is completed. */
    RESPONSE_COMPLETED("response.completed"),

    /** Occurs when a Response fails. */
    RESPONSE_FAILED("response.failed"),

    /** Occurs when a Response ends with an incomplete status. */
    RESPONSE_INCOMPLETE("response.incomplete"),

    /** Occurs when an error is emitted. */
    ERROR("error"),

    /** Occurs when a new output item is added. */
    RESPONSE_OUTPUT_ITEM_ADDED("response.output_item.added"),

    /** Occurs when an output item is completed. */
    RESPONSE_OUTPUT_ITEM_DONE("response.output_item.done"),

    /** Occurs when a new content part is added. */
    RESPONSE_CONTENT_PART_ADDED("response.content_part.added"),

    /** Occurs when a content part is completed. */
    RESPONSE_CONTENT_PART_DONE("response.content_part.done"),

    /** Occurs when the text of an output text part is streamed. */
    RESPONSE_OUTPUT_TEXT_DELTA("response.output_text.delta"),

    /** Occurs when the text of an output text part is finalized. */
    RESPONSE_OUTPUT_TEXT_DONE("response.output_text.done"),

    /** Occurs when an annotation is added to an output text part. */
    RESPONSE_OUTPUT_TEXT_ANNOTATION_ADDED("response.output_text.annotation.added"),

    /** Occurs when the refusal text is streamed. */
    RESPONSE_REFUSAL_DELTA("response.refusal.delta"),

    /** Occurs when the refusal text is finalized. */
    RESPONSE_REFUSAL_DONE("response.refusal.done"),

    /** Occurs when reasoning text is streamed. */
    RESPONSE_REASONING_TEXT_DELTA("response.reasoning_text.delta"),

    /** Occurs when reasoning text is finalized. */
    RESPONSE_REASONING_TEXT_DONE("response.reasoning_text.done"),

    /** Occurs when a reasoning summary part is added. */
    RESPONSE_REASONING_SUMMARY_PART_ADDED("response.reasoning_summary_part.added"),

    /** Occurs when a reasoning summary part is completed. */
    RESPONSE_REASONING_SUMMARY_PART_DONE("response.reasoning_summary_part.done"),

    /** Occurs when reasoning summary text is streamed. */
    RESPONSE_REASONING_SUMMARY_TEXT_DELTA("response.reasoning_summary_text.delta"),

    /** Occurs when reasoning summary text is finalized. */
    RESPONSE_REASONING_SUMMARY_TEXT_DONE("response.reasoning_summary_text.done"),

    /** Occurs when the arguments of a function call are streamed. */
    RESPONSE_FUNCTION_CALL_ARGUMENTS_DELTA("response.function_call_arguments.delta"),

    /** Occurs when the arguments of a function call are finalized. */
    RESPONSE_FUNCTION_CALL_ARGUMENTS_DONE("response.function_call_arguments.done"),

    /** Occurs when the input of a custom tool call is streamed. */
    RESPONSE_CUSTOM_TOOL_CALL_INPUT_DELTA("response.custom_tool_call_input.delta"),

    /** Occurs when the input of a custom tool call is finalized. */
    RESPONSE_CUSTOM_TOOL_CALL_INPUT_DONE("response.custom_tool_call_input.done"),

    /** Occurs when a file search call is in progress. */
    RESPONSE_FILE_SEARCH_CALL_IN_PROGRESS("response.file_search_call.in_progress"),

    /** Occurs when a file search call is searching. */
    RESPONSE_FILE_SEARCH_CALL_SEARCHING("response.file_search_call.searching"),

    /** Occurs when a file search call is completed. */
    RESPONSE_FILE_SEARCH_CALL_COMPLETED("response.file_search_call.completed"),

    /** Occurs when a web search call is in progress. */
    RESPONSE_WEB_SEARCH_CALL_IN_PROGRESS("response.web_search_call.in_progress"),

    /** Occurs when a web search call is searching. */
    RESPONSE_WEB_SEARCH_CALL_SEARCHING("response.web_search_call.searching"),

    /** Occurs when a web search call is completed. */
    RESPONSE_WEB_SEARCH_CALL_COMPLETED("response.web_search_call.completed"),

    /** Occurs when a code interpreter call is in progress. */
    RESPONSE_CODE_INTERPRETER_CALL_IN_PROGRESS("response.code_interpreter_call.in_progress"),

    /** Occurs when a code interpreter call is interpreting. */
    RESPONSE_CODE_INTERPRETER_CALL_INTERPRETING("response.code_interpreter_call.interpreting"),

    /** Occurs when a code interpreter call is completed. */
    RESPONSE_CODE_INTERPRETER_CALL_COMPLETED("response.code_interpreter_call.completed"),

    /** Occurs when code from a code interpreter call is streamed. */
    RESPONSE_CODE_INTERPRETER_CALL_CODE_DELTA("response.code_interpreter_call_code.delta"),

    /** Occurs when code from a code interpreter call is finalized. */
    RESPONSE_CODE_INTERPRETER_CALL_CODE_DONE("response.code_interpreter_call_code.done"),

    /** Occurs when an image generation call is in progress. */
    RESPONSE_IMAGE_GENERATION_CALL_IN_PROGRESS("response.image_generation_call.in_progress"),

    /** Occurs when an image generation call is generating. */
    RESPONSE_IMAGE_GENERATION_CALL_GENERATING("response.image_generation_call.generating"),

    /** Occurs when a partial image is emitted by an image generation call. */
    RESPONSE_IMAGE_GENERATION_CALL_PARTIAL_IMAGE("response.image_generation_call.partial_image"),

    /** Occurs when an image generation call is completed. */
    RESPONSE_IMAGE_GENERATION_CALL_COMPLETED("response.image_generation_call.completed"),

    /** Occurs when audio is streamed. */
    RESPONSE_AUDIO_DELTA("response.audio.delta"),

    /** Occurs when audio is finalized. */
    RESPONSE_AUDIO_DONE("response.audio.done"),

    /** Occurs when an audio transcript is streamed. */
    RESPONSE_AUDIO_TRANSCRIPT_DELTA("response.audio.transcript.delta"),

    /** Occurs when an audio transcript is finalized. */
    RESPONSE_AUDIO_TRANSCRIPT_DONE("response.audio.transcript.done"),

    /** Occurs when an MCP call is in progress. */
    RESPONSE_MCP_CALL_IN_PROGRESS("response.mcp_call.in_progress"),

    /** Occurs when MCP call arguments are streamed. */
    RESPONSE_MCP_CALL_ARGUMENTS_DELTA("response.mcp_call_arguments.delta"),

    /** Occurs when MCP call arguments are finalized. */
    RESPONSE_MCP_CALL_ARGUMENTS_DONE("response.mcp_call_arguments.done"),

    /** Occurs when an MCP call is completed. */
    RESPONSE_MCP_CALL_COMPLETED("response.mcp_call.completed"),

    /** Occurs when an MCP call fails. */
    RESPONSE_MCP_CALL_FAILED("response.mcp_call.failed"),

    /** Occurs when MCP tool listing is in progress. */
    RESPONSE_MCP_LIST_TOOLS_IN_PROGRESS("response.mcp_list_tools.in_progress"),

    /** Occurs when MCP tool listing is completed. */
    RESPONSE_MCP_LIST_TOOLS_COMPLETED("response.mcp_list_tools.completed"),

    /** Occurs when MCP tool listing fails. */
    RESPONSE_MCP_LIST_TOOLS_FAILED("response.mcp_list_tools.failed"),

    /** Occurs when a shell call command is added. */
    RESPONSE_SHELL_CALL_COMMAND_ADDED("response.shell_call_command.added"),

    /** Occurs when shell call command output is streamed. */
    RESPONSE_SHELL_CALL_COMMAND_DELTA("response.shell_call_command.delta"),

    /** Occurs when a shell call command is finalized. */
    RESPONSE_SHELL_CALL_COMMAND_DONE("response.shell_call_command.done"),

    /** Occurs when shell call output content is streamed. */
    RESPONSE_SHELL_CALL_OUTPUT_CONTENT_DELTA("response.shell_call_output_content.delta"),

    /** Occurs when shell call output content is finalized. */
    RESPONSE_SHELL_CALL_OUTPUT_CONTENT_DONE("response.shell_call_output_content.done"),

    /** Occurs when a steer request is accepted. */
    RESPONSE_STEER_ACCEPTED("response.steer.accepted"),

    /** Occurs when a steer request fails. */
    RESPONSE_STEER_FAILED("response.steer.failed"),

    /** Occurs when a steer request is pending. */
    RESPONSE_STEER_PENDING("response.steer.pending"),

    /** Occurs while a response is being compacted. */
    RESPONSE_COMPACTION_COMPACTING("response.compaction.compacting"),

    /** Occurs when the event type is not recognized. */
    UNKNOWN("unknown");

    public companion object {
        /**
         * Returns the [ResponseStreamEventType] matching [event], or [UNKNOWN] if unrecognized.
         */
        public fun fromEvent(event: String): ResponseStreamEventType =
            entries.find { it.event == event } ?: UNKNOWN
    }
}
