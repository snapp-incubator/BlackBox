package cab.snapp.blackBox.loki

import com.google.gson.annotations.SerializedName

internal data class LokiPushRequest(
    @SerializedName("streams") val streams: List<Stream>
) {
    data class Stream(
        @SerializedName("labels") val labels: String,
        @SerializedName("entries") val entities: List<Entity>
    ) {
        data class Label(@SerializedName("cab") val appName: String)
        data class Entity(
            @SerializedName("ts") val ts: String,
            @SerializedName("line") val message: String
        )
    }
}