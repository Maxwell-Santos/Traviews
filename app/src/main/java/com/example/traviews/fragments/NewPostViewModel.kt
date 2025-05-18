package com.example.traviews.fragments

import android.app.Application
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.traviews.model.PublishPostRequest
import com.example.traviews.model.PublishPostResponse
import com.example.traviews.network.TraviewsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


interface NewPostUiState {
    data class Success(val postPublished: PublishPostResponse) : NewPostUiState
    object Initial : NewPostUiState
    object Error : NewPostUiState
    object Loading : NewPostUiState
}

data class ImageContent (val filename: String, val fileUri: Uri)

@HiltViewModel
class NewPostViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _newPostUiState = MutableStateFlow<NewPostUiState>(NewPostUiState.Initial)
    val newPostUiState: StateFlow<NewPostUiState> = _newPostUiState

    val images: MutableList<ImageContent> = mutableListOf()
    lateinit var dateFormatedToSave: String

    fun publishPost(post: PublishPostRequest) {
        viewModelScope.launch {
            _newPostUiState.value = try {
                NewPostUiState.Loading

                val result = TraviewsApi.retrofitService.publishPost(post)

                for ((index, fileToUpload) in result.filesToUpload.withIndex()) {
                    uploadPostImage(images[index].fileUri, fileToUpload.pathToUpload)
                }

                NewPostUiState.Success(result)
            } catch (e: IOException) {
                NewPostUiState.Error
            } catch (e: HttpException) {
                NewPostUiState.Error
            }
        }
    }

    fun uploadPostImage(fileUri: Uri, apiUrl: String) {
        viewModelScope.launch {
            try {
                val result = uploadFileToServer(fileUri, apiUrl)
                println("Upload success: $result")
            } catch (e: Exception) {
                println("Upload failed: ${e.message}")
            }
        }
    }

    private suspend fun uploadFileToServer(
        fileUri: Uri,
        apiUrl: String
    ): String = withContext(Dispatchers.IO) {
        var connection: HttpsURLConnection? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null

        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(fileUri) ?: "application/octet-stream"
            val fileExtension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType) ?: "bin"

            // Configura a conexão HTTP
            val url = URL(apiUrl)
            connection = url.openConnection() as HttpsURLConnection
            connection.apply {
                requestMethod = "PUT"
                doOutput = true
                doInput = true
                connectTimeout = 30000
                readTimeout = 30000
                setRequestProperty("Content-Type", mimeType)
                setRequestProperty(
                    "Content-Disposition",
                    "attachment; filename=\"upload.${fileExtension}\""
                )
            }

            // Debug: log das informações do arquivo
            val fileSize = contentResolver.openAssetFileDescriptor(fileUri, "r")?.use { fd ->
                fd.length
            } ?: 0
            println("Iniciando upload de $fileExtension ($mimeType) - Tamanho: $fileSize bytes")

            // Realiza o upload em chunks
            contentResolver.openInputStream(fileUri)?.use { input ->
                connection.outputStream.use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                    output.flush()
                }
            } ?: throw IOException("Failed to open input stream")

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val errorMessage = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    ?: "No error message"
                throw IOException("HTTP error $responseCode: $errorMessage")
            }

            return@withContext connection.inputStream.bufferedReader().use { it.readText() }

        } catch (e: Exception) {
            println("Erro durante upload: ${e.stackTraceToString()}")
            throw e
        } finally {
            try {
                outputStream?.close()
            } catch (e: Exception) {
                println("Erro ao fechar outputStream: ${e.message}")
            }
            try {
                inputStream?.close()
            } catch (e: Exception) {
                println("Erro ao fechar inputStream: ${e.message}")
            }
            try {
                connection?.disconnect()
            } catch (e: Exception) {
                println("Erro ao desconectar: ${e.message}")
            }
        }
    }
}

//    private suspend fun uploadFileToServer(
//        fileUri: Uri,
//        apiUrl: String
//    ): String = withContext(Dispatchers.IO) {
//        var connection: HttpsURLConnection? = null
//        var attempts = 0
//        val maxAttempts = 3
//
//        while (attempts < maxAttempts) {
//            try {
//                val url = URL(apiUrl)
//                connection = url.openConnection() as HttpsURLConnection
//                connection.apply {
//                    requestMethod = "PUT"
//                    doOutput = true
//                    doInput = true
//                    connectTimeout = 60000
//                    readTimeout = 60000
//                    setRequestProperty("Content-Type", "application/octet-stream")
//                    setRequestProperty("Authorization", "Bearer ${AuthTokenRepositoryImpl.get()}")
//                    setRequestProperty("Connection", "keep-alive")
//                }
//
//                context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
//                    connection.outputStream.use { outputStream ->
//                        val buffer = ByteArray(8192)
//                        var bytesRead: Int
//                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                            outputStream.write(buffer, 0, bytesRead)
//                        }
//                        outputStream.flush()
//                    }
//                } ?: throw IOException("Failed to open input stream")
//
//                val responseCode = connection.responseCode
//                if (responseCode !in 200..299) {
//                    throw IOException("HTTP error $responseCode: ${connection.errorStream?.bufferedReader()?.use { it.readText() }}")
//                }
//
//                return@withContext connection.inputStream.bufferedReader().use { it.readText() }
//
//            } catch (e: SocketException) {
//                attempts++
//                if (attempts == maxAttempts) {
//                    throw IOException("Upload failed after $maxAttempts attempts", e)
//                }
//
////                delay(1000 * attempts)
//
//                Thread.sleep(1000)
//            } finally {
//                connection?.disconnect()
//            }
//        }
//        throw IOException("Upload failed unexpectedly")
//    }