package com.example.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

const val GITHUB_OWNER = "VK2012K2012"
const val GITHUB_REPOSITORY = "Card-Game-App"
const val GITHUB_RELEASES_URL =
    "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPOSITORY/releases/latest"

const val APK_MIME_TYPE = "application/vnd.android.package-archive"

data class GitHubReleaseAsset(
    val name: String,
    val downloadUrl: String,
    val size: Long,
    val digest: String?,
)

data class GitHubReleaseInfo(
    val tagName: String,
    val title: String,
    val notes: String,
    val pageUrl: String,
    val apk: GitHubReleaseAsset,
)

object GitHubUpdateChecker {
    suspend fun fetchLatestRelease(): GitHubReleaseInfo = withContext(Dispatchers.IO) {
        val connection = (URL(GITHUB_RELEASES_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 20_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2026-03-10")
        }
        try {
            val status = connection.responseCode
            if (status !in 200..299) throw IOException("GitHub returned HTTP $status")
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parseRelease(body)
        } finally {
            connection.disconnect()
        }
    }

    fun parseRelease(json: String): GitHubReleaseInfo {
        val root = JSONObject(json)
        val assets = root.optJSONArray("assets") ?: throw IOException("Release has no assets")
        val apkObject = (0 until assets.length())
            .asSequence()
            .map { assets.optJSONObject(it) }
            .filterNotNull()
            .firstOrNull { it.optString("name").equals("app-release.apk", ignoreCase = true) }
            ?: (0 until assets.length())
                .asSequence()
                .map { assets.optJSONObject(it) }
                .filterNotNull()
                .firstOrNull { it.optString("name").endsWith(".apk", ignoreCase = true) }
            ?: throw IOException("Release has no APK asset")

        val downloadUrl = apkObject.optString("browser_download_url")
        if (downloadUrl.isBlank()) throw IOException("APK asset has no download URL")

        return GitHubReleaseInfo(
            tagName = root.optString("tag_name").ifBlank { throw IOException("Release has no tag") },
            title = root.optString("name").ifBlank { root.optString("tag_name") },
            notes = root.optString("body"),
            pageUrl = root.optString("html_url"),
            apk = GitHubReleaseAsset(
                name = apkObject.optString("name"),
                downloadUrl = downloadUrl,
                size = apkObject.optLong("size", -1L),
                digest = apkObject.optString("digest").takeIf { it.isNotBlank() },
            ),
        )
    }

    fun selectApkAsset(assets: List<GitHubReleaseAsset>): GitHubReleaseAsset =
        assets.firstOrNull { it.name.equals("app-release.apk", ignoreCase = true) }
            ?: assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
            ?: throw IOException("Release has no APK asset")

    fun isNewerVersion(remote: String, current: String): Boolean = compareVersions(remote, current) > 0

    fun compareVersions(left: String, right: String): Int {
        val a = versionParts(left)
        val b = versionParts(right)
        val count = maxOf(a.size, b.size)
        for (index in 0 until count) {
            val av = a.getOrElse(index) { 0 }
            val bv = b.getOrElse(index) { 0 }
            if (av != bv) return av.compareTo(bv)
        }
        return 0
    }

    private fun versionParts(value: String): List<Int> =
        Regex("\\d+").findAll(value).map { it.value.toIntOrNull() ?: 0 }.toList()

    suspend fun downloadApk(context: Context, asset: GitHubReleaseAsset): File = withContext(Dispatchers.IO) {
        val target = File(context.cacheDir, "cardgamehub-update.apk")
        if (target.exists()) target.delete()
        val connection = (URL(asset.downloadUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 30_000
            instanceFollowRedirects = true
            setRequestProperty("Accept", "application/octet-stream")
        }
        try {
            val status = connection.responseCode
            if (status !in 200..299) throw IOException("Download returned HTTP $status")
            connection.inputStream.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            verifyDigest(target, asset.digest)
            target
        } catch (error: Throwable) {
            target.delete()
            throw error
        } finally {
            connection.disconnect()
        }
    }

    private fun verifyDigest(file: File, digest: String?) {
        if (digest.isNullOrBlank()) return
        val expected = digest.substringAfter(':', digest).lowercase()
        val actual = MessageDigest.getInstance("SHA-256").digest(file.readBytes())
            .joinToString("") { "%02x".format(it) }
        if (actual != expected) throw IOException("Downloaded APK digest does not match GitHub asset")
    }

    fun launchInstaller(context: Context, apk: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apk,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, APK_MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun canRequestInstallPackages(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()
}
