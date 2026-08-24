# GitHub updater guidance

Official references consulted:

1. GitHub REST API releases: https://docs.github.com/en/rest/releases/releases
   The public `GET /repos/{owner}/{repo}/releases/latest` endpoint returns the latest published non-draft, non-prerelease release, including `tag_name`, `html_url`, `body`, and release `assets`. Assets include `browser_download_url`, `name`, `size`, and optional `digest`.

2. Android secure file sharing / FileProvider: https://developer.android.com/training/secure-file-sharing/setup-sharing
   APK files downloaded into the app cache should be exposed to the installer through a non-exported AndroidX FileProvider with a narrowly scoped cache path and temporary URI permission.

3. Android Intent reference: https://developer.android.com/reference/android/content/Intent
   The standard installer flow can be launched with an install/view intent carrying the APK content URI, APK MIME type, and `FLAG_GRANT_READ_URI_PERMISSION`; Android retains user confirmation and does not allow silent installation for a normal app.

Implementation constraints for CardGameHub:

- Public repository: VK2012K2012/Card-Game-App.
- Check only on explicit user action from About app; no background polling.
- Compare semantic numeric components from GitHub `tag_name` against `BuildConfig.VERSION_NAME`.
- Download only an `.apk` release asset into `cacheDir`.
- Verify GitHub's optional SHA-256 asset digest before launching installer.
- Use HTTPS, timeouts, clear error states, and delete partial downloads.
- Current minSdk is 24; REQUEST_INSTALL_PACKAGES handling is relevant on Android O+.
