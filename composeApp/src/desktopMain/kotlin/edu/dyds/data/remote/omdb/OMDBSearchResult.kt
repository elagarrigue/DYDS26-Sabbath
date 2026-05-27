package edu.dyds.data.remote.omdb

import edu.dyds.data.external.omdb.OMDBMovie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OMDBSearchResult(
    @SerialName("Search")
    val search: List<OMDBMovie> = emptyList(),
    @SerialName("Response")
    val response: String,
    @SerialName("totalResults")
    val totalResults: String = "0",
    @SerialName("Error")
    val error: String? = null,
)

