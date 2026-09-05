package com.example.transpose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.data.newpipe.repository.base.NewPipeManager
import com.example.domain.model.youtube.video.Video
import com.example.main.MainScreen
import com.example.media.manager.MediaPlaybackManager
import com.example.ui.theme.TransposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mediaPlaybackManager: MediaPlaybackManager

    @Inject
    lateinit var newPipeManager: NewPipeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TransposeTheme {
                MainScreen()
            }
        }

        handleExternalIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleExternalIntent(intent)
    }

    private fun handleExternalIntent(intent: Intent?) {
        val candidate = when (intent?.action) {
            Intent.ACTION_VIEW -> intent.dataString
            Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT)
            else -> null
        }

        val youtubeUrl = extractYouTubeUrl(candidate) ?: return
        val videoId = extractVideoId(youtubeUrl) ?: return

        lifecycleScope.launch {
            val video = withContext(Dispatchers.IO) {
                runCatching { buildVideo(videoId) }.getOrNull()
            } ?: return@launch

            mediaPlaybackManager.mediaControllerFlow.filterNotNull().first()
            mediaPlaybackManager.playSingleVideo(video)
        }
    }

    private fun buildVideo(videoId: String): Video {
        val extractor = newPipeManager.getStreamExtractor(videoId)
        extractor.fetchPage()
        val uploaderId = runCatching {
            newPipeManager.getChannelId(extractor.uploaderUrl)
        }.getOrDefault("")

        return Video(
            id = extractor.id,
            title = extractor.name,
            thumbnailUrl = extractor.thumbnails.firstOrNull()?.url,
            description = extractor.description.content,
            publishTimestamp = extractor.uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli(),
            infoType = "Stream",
            uploaderName = extractor.uploaderName,
            uploaderUrl = uploaderId,
            uploaderAvatarUrl = extractor.uploaderAvatars.firstOrNull()?.url,
            uploaderVerified = null,
            duration = extractor.length,
            viewCount = extractor.viewCount,
            textualUploadDate = extractor.textualUploadDate,
            streamType = null,
            shortFormContent = false
        )
    }

    private fun extractYouTubeUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val match = YOUTUBE_URL_REGEX.find(text)?.value ?: return null
        return match.trimEnd('.', ',', ';', ':', ')', ']', '}', '>')
    }

    private fun extractVideoId(url: String): String? {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return null
        val host = uri.host?.lowercase() ?: return null

        return when {
            host == "youtu.be" -> uri.pathSegments.firstOrNull()
            host == "youtube.com" || host == "www.youtube.com" || host == "m.youtube.com" -> {
                uri.getQueryParameter("v")
                    ?: uri.pathSegments
                        .takeIf { it.size >= 2 && it.first() in YOUTUBE_VIDEO_PATHS }
                        ?.get(1)
            }
            else -> null
        }?.takeIf { it.isNotBlank() }
    }

    companion object {
        private val YOUTUBE_URL_REGEX = Regex(
            "https?://(?:www\\.|m\\.)?(?:youtube\\.com|youtu\\.be)/[^\\s]+",
            RegexOption.IGNORE_CASE
        )
        private val YOUTUBE_VIDEO_PATHS = setOf("shorts", "embed", "live")
    }
}
