package com.example.hetrogeneousjsonparsing

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okhttp3.MediaType
import retrofit2.Retrofit


private val TAG = "TAG"

@Serializable
data class VideoProject(
    val layers:List<Layer>
)


@Serializable
abstract class Layer{
    abstract val startTime:Long
    abstract val duration:Long
}

@Serializable
@SerialName("text")
data class TextLayer(
    val text:String,
    override val startTime: Long,
    override val duration: Long
):Layer()

@Serializable
@SerialName("video")
data class VideoLayer(
    val source:String,
    override val duration: Long,
    override val startTime: Long
):Layer()


@Serializable
@SerialName("audio")
data class AudioLayer(
    val source:String,
    val volume:Int,
    override val startTime: Long,
    override val duration: Long
):Layer()



object ProjectSerializer{

    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Layer::class){
                subclass(TextLayer::class,TextLayer.serializer())
                subclass(VideoLayer::class,VideoLayer.serializer())
                subclass(AudioLayer::class,AudioLayer.serializer())
            }
        }
    }
}


fun polymorphicResponse(){
    val str = "{\n" +
            " \"layers\": [\n" +
            "   {\n" +
            "     \"type\": \"text\",\n" +
            "     \"startTime\": 0,\n" +
            "     \"duration\": 10,\n" +
            "     \"text\": \"hello\"\n" +
            "   },\n" +
            "   {\n" +
            "     \"type\": \"video\",\n" +
            "     \"startTime\": 0,\n" +
            "     \"duration\": 10,\n" +
            "     \"source\": \"source.mp4\"\n" +
            "   },\n" +
            "   {\n" +
            "     \"type\": \"audio\",\n" +
            "     \"startTime\": 0,\n" +
            "     \"duration\": 7,\n" +
            "     \"source\": \"audio_source.mp4\",\n" +
            "     \"volume\": 3\n" +
            "   }\n" +
            " ]\n" +
            "}"


   val decode= ProjectSerializer.json.decodeFromString<VideoProject>(string=str)
    Log.d(TAG, "polymorphicResponse: $decode")

    val contentType=MediaType.parse("application/json")

    Retrofit.Builder().addConverterFactory(ProjectSerializer.json.asConverterFactory(contentType!!))

}






















