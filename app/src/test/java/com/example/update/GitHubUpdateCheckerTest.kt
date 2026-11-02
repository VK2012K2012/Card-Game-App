package com.example.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GitHubUpdateCheckerTest {
    @Test
    fun comparesReleaseVersionsNumerically() {
        assertTrue(GitHubUpdateChecker.isNewerVersion("v1.1.0", "1.0"))
        assertTrue(GitHubUpdateChecker.isNewerVersion("1.10", "1.9"))
        assertFalse(GitHubUpdateChecker.isNewerVersion("v1.1", "1.1.0"))
        assertEquals(0, GitHubUpdateChecker.compareVersions("release-1.1", "v1.1.0"))
    }

    @Test
    fun selectsPreferredApkAsset() {
        val selected = GitHubUpdateChecker.selectApkAsset(
            listOf(
                GitHubReleaseAsset("notes.txt", "https://example.com/notes.txt", 12L, null),
                GitHubReleaseAsset("app-release.apk", "https://example.com/app-release.apk", 3456L, "sha256:abcdef"),
            ),
        )

        assertEquals("app-release.apk", selected.name)
        assertEquals("sha256:abcdef", selected.digest)
        assertEquals(3456L, selected.size)
    }

    @Test
    fun explainsWhenInstalledBuildIsAheadOfGitHub() {
        assertEquals(
            "Installed build 1.1.7 is newer than the latest GitHub release v1.1.2; no newer update has been published yet.",
            GitHubUpdateChecker.upToDateMessage("1.1.7", "v1.1.2"),
        )
    }

    @Test
    fun reportsLatestPublishedVersionWhenTagsMatch() {
        assertEquals(
            "You have the latest published version (v1.1.7).",
            GitHubUpdateChecker.upToDateMessage("1.1.7", "v1.1.7"),
        )
    }
}
