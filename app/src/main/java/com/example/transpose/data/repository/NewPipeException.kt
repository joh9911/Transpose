package com.example.transpose.data.repository

sealed class NewPipeException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    abstract val methodName: String
    class ParsingException(override val methodName: String, cause: Throwable? = null): NewPipeException("Method: ${methodName}, Parsing Problem", cause)
    class PageCannotBeLoaded(override val methodName: String, cause: Throwable? = null): NewPipeException("Method: ${methodName}, Page Cannot Be Loaded", cause)
    class InvalidPlaylistIdOrUrl(override val methodName: String, playlistId: String, cause: Throwable? = null) :
        NewPipeException("Method: ${methodName}, Invalid playlist ID or URL: $playlistId", cause)
    class ExtractionFailed(override val methodName: String, cause: Throwable? = null) :
        NewPipeException("Method: ${methodName}, Failed to extract", cause)
    class NetworkError(override val methodName: String, cause: Throwable? = null) :
        NewPipeException("Method: ${methodName}, Network error occurred", cause)
    class PlaylistNotFound(override val methodName: String, cause: Throwable? = null) :
        NewPipeException("Method: ${methodName}, Playlist not found", cause)
    class PlaylistItemsFetchFailed(override val methodName: String, cause: Throwable? = null) :
        NewPipeException("Method: ${methodName}, Failed to fetch playlist items", cause)
    class UnsupportedOperationException(override val methodName: String, cause: Throwable? = null):
        NewPipeException("Method: ${methodName}, Unsupported Operation", cause)
    class UnknownError(override val methodName: String, cause: Throwable? = null) :
        NewPipeException("Method: ${methodName}, An unknown error occurred", cause)
}