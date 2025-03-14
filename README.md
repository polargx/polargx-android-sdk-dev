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

      For Mac:
      ```
      ./gradlew signingReport
      
      ```
      For Windows:
      ```
      gradlew signingReport
      
      ```
    - Use _SHA-256 fingerprint_ for _SHA256 Cert Fingerprints_.
  + Scheme URI: Help your link opens app if your app was installed and can't be opened by _App Links_.
    Ex: `yourapp_schemeurl://`
  
### 2. Adding PolarGX SDK
- Open `build.gradle` (Module: app) and add the following line inside dependencies:

  ```
  dependencies {
      implementation "com.github.infinitech-dev:LinkAttribution-AndroidSDK:1.0.8"
  }
  ```
- Click **Sync Now** in the top-right corner of Android Studio or go to **File → Sync Project with Gradle Files**.
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
  
          <!-- Replace subdomain with your domain from PolarGX -->
          <data android:scheme="https" />
          <data android:host="{subdomain}.gxlnk.com" />
          <data android:host="{subdomain}-alternate.gxlnk.com" />
      </intent-filter>
  </activity>
  ```
### 4. Use PolarGX SDK
- Get _App Id_ and _API Key_ from https://app.polargx.com.
- In MyApplication.kt:

  ```
  override fun onCreate() {
      super.onCreate()
  
      Polar.isLoggingEnabled = true
      Polar.initialize(
          application = this,
          appId = YOUR_APP_ID,
          apiKey = YOUR_API_KEY
      )
  }
  ```
- In SplashActivity.kt:

   ```
   private val mPolarListener = PolarInitListener { attributes, error ->
        // Handle deep link
   }

   override fun onStart() {
        super.onStart()
        Polar.bind(
            activity = this,
            uri = intent?.data,
            listener = mPolarListener
        )
   }

   override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        if (intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra("branch_force_new_session", false)) {
            Polar.reBind(
                activity = this,
                uri = intent.data,
                listener = mPolarListener
            )
        }
   }
   ```
