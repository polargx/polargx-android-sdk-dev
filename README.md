## PolarGX Android SDK Installation Guide
### 1. Create and setup Polar app
- Register PolarGX account at https://app.polargx.com, after signup `unnamed` app has been created automatically.
- Setting your app in _App Settings > App Information_.
- Create an API Key in _App Settings > API Keys_ with _Mobile apps / frontend_ purpose.
- Configure your domain in _Link Attribution > Configuration > Link domain section_ with:
  + Default Link Domain
  + Alternative Link Domain
- Configure your Android Redirects in _Link Attribution > Configuration > Required Redirects section > Android Redirects_ with:
  + Google Play Search or Custom URL: Help your link redirects to Google Play or your custom url if your app hasn't been installed.
  + App Link: Help your link opens app immediately if your app was installed.
    - Open Android Studio.
    - Run the following command in the Terminal. The output will display the SHA256 fingerprint for both debug and release builds.

      ✅ For Mac:
      ```
      ./gradlew signingReport
      
      ```
      ✅ For Windows:
      ```
      gradlew signingReport
      
      ```
    - Use _SHA-256_ for _SHA256 Cert Fingerprints_.
  + Scheme URI: Help your link opens app if your app was installed and can't be opened by _App Links_.
    Ex: `yourapp_schemeurl://`
  
### 2. Adding PolarGX SDK
#### 2.1. Using Libraries from Maven/JCenter (Online Dependency)
- Comming soon.
#### 2.2. Using `.jar` Files (Java Library)
- Copy the `.jar` file into the libs directory (`app/libs`).
- Add the library to `build.gradle`:
```
dependencies {
    implementation files('libs/mylibrary.jar')
}
```
- Sync Gradle.
#### 2.3. Using `.aar` Files (Android Archive Library)
- Copy the `.aar` file into the `libs/` folder.
- Add the dependency in `build.gradle`:
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```
- Sync Gradle.
#### 2.4. Using a Local Library Module (Internal Dependency)
- Copy the library module folder (`module_library/`) into the project directory.
- Open `settings.gradle` and register the module:
```
include ':module_library'

```
- Add the dependency to `build.gradle` in the `app` module:
```
dependencies {
    implementation project(':module_library')
}
```
- Sync Gradle.
### 3. Configure AndroidManifest.xml
- Open AndroidManifest.xml and add an `intent-filter` to the activity you want to open when a user clicks the App Link:
```
<activity
    android:name=".MainActivity"
    android:exported="true">
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!-- Replace with the domain from PolarGX -->
        <data
            android:scheme="https"
            android:host="yourbrand.com"
            android:pathPrefix="/link/" />
    </intent-filter>
</activity>
```
### 4. Use PolarGX SDK
- Get _App Id_ and _API Key_ from https://app.polargx.com.
- In MyApplication.kt:
  ```
  override fun onCreate() {
    super.onCreate()

    // Initialize Polar
    Polar.initialize(
      application = this,
      appId = YOUR_APP_ID,
      apiKey = YOUR_API_KEY
    )
  }
  ```
