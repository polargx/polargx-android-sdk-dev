package com.library.core.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.shared.AppConstants
import com.app.shared.logger.DebugLogger
import com.library.core.R
import com.library.core.activity.listener.OnBackPressedListener
import com.library.core.activity.listener.OnDispatchTouchEventListener
import com.library.core.view.popup.OneOptionView
import com.library.core.activity.listener.OnFragmentStackChangedListener
import com.library.core.application.BaseApplication
import com.library.core.extension.getDisplayMessage
import com.library.core.extension.getTopView
import com.library.core.extension.hideKeyboard
import com.library.core.extension.registerBroadcastReceiver
import com.library.core.extension.showKeyboard
import com.library.core.fragment.navigator.FragmentNavigator
import com.library.core.view.popup.LoadingView
import org.koin.android.ext.android.inject

abstract class BaseActivity(
    contentLayoutId: Int
) : AppCompatActivity(contentLayoutId),
    BaseActivityViewModel.LiveEvent,
    OnFragmentStackChangedListener {

    companion object {
        const val TAG = ">>>BaseActivity"
    }

    protected var mIsBackPressed: Boolean = false
    private var mBroadcastReceiver: BroadcastReceiver? = null

    var isFullLayout: Boolean? = true
    protected val mNavigator by lazy { FragmentNavigator(this, supportFragmentManager) }

    val mRootLayout by lazy {
        findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0) as ViewGroup
    }

    private val mApplication by inject<BaseApplication>()


    abstract fun getViewModel(): BaseActivityViewModel<out BaseActivityViewModel.LiveEvent>?

    override fun onAttachedToWindow() {
        DebugLogger.d(TAG, "onAttachedToWindow: $this")
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugLogger.d(TAG, "onCreate: $this, savedInstanceState = $savedInstanceState")
        init(savedInstanceState)
//        mApplication.onActivityCreated(this, savedInstanceState)
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DebugLogger.d(TAG, "onBackPressed: $this")
                if ((mRootLayout.getTopView() as? OnBackPressedListener)?.onBackPressed() == true) return
                if ((getCurrentFragment() as? OnBackPressedListener)?.onBackPressed() == true) return

                mIsBackPressed = true
                popFragment(true)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        mApplication.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        DebugLogger.d(TAG, "onConfigurationChanged: $this, newConfig = $newConfig")
    }

    override fun onContentChanged() {
        DebugLogger.d(TAG, "onContentChanged: $this")
        super.onContentChanged()
    }

    override fun onStart() {
        DebugLogger.d(TAG, "onStart: $this")
        super.onStart()
    }

    override fun onResume() {
        DebugLogger.d(TAG, "onResume: $this")
        super.onResume()
    }

    override fun onPause() {
        DebugLogger.d(TAG, "onPause: $this")
        super.onPause()
    }

    override fun onRestart() {
        DebugLogger.d(TAG, "onRestart: $this")
        super.onRestart()
    }

    override fun onStop() {
        DebugLogger.d(TAG, "onStop: $this")
        super.onStop()
    }

    override fun onDestroy() {
        DebugLogger.d(TAG, "onDestroy: $this")
        unregisterBroadcasts()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        DebugLogger.d(TAG, "onNewIntent: $this, intent = $intent")
        super.onNewIntent(intent)
//        mApplication.onActivityNewIntent(this, intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        DebugLogger.d(TAG, "onSaveInstanceState: $this, outState = $outState")
        super.onSaveInstanceState(Bundle())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        DebugLogger.d(
            TAG,
            "onRestoreInstanceState: $this, savedInstanceState = $savedInstanceState"
        )
        savedInstanceState.clear()
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        DebugLogger.d(
            TAG,
            "onSaveInstanceState: $this, outState = $outState, outPersistentState = $outPersistentState"
        )
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onDetachedFromWindow() {
        DebugLogger.d(TAG, "onDetachedFromWindow: $this")
        super.onDetachedFromWindow()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if ((getCurrentFragment() as? OnDispatchTouchEventListener)?.onDispatchTouchEvent(ev) == true) return true
        return super.dispatchTouchEvent(ev)
    }

    override fun finish() {
//        DebugLogger.d(TAG, "finish: $this")
        super.finish()
    }

    override fun finishAfterTransition() {
        DebugLogger.d(TAG, "finishAfterTransition: $this")
        super.finishAfterTransition()
    }

    override fun finishAffinity() {
        DebugLogger.d(TAG, "finishAffinity: $this")
        super.finishAffinity()
    }

    override fun finishActivity(requestCode: Int) {
        DebugLogger.d(TAG, "finishActivity: $this, requestCode = $requestCode")
        super.finishActivity(requestCode)
    }

    private fun registerBroadcasts() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    DebugLogger.d(TAG, "onReceive: action=${intent?.action}")
                    when (intent?.action) {
                        AppConstants.AppVersion.UPDATE_APP_VERSION_BROADCAST_ACTION -> {
//                            startActivity(AppActions.openUpdateVersionIntent(this@BaseActivity))
                        }

                        AppConstants.Broadcast.ACTIVITY_BACKGROUND_COLOR_ENABLE_CHANGED -> {
                            applyTheme()
                        }
                    }
                }
            }
        }
        registerBroadcastReceiver(
            mBroadcastReceiver,
            IntentFilter(AppConstants.AppVersion.UPDATE_APP_VERSION_BROADCAST_ACTION)
        )
        registerBroadcastReceiver(
            mBroadcastReceiver,
            IntentFilter(AppConstants.Broadcast.ACTIVITY_BACKGROUND_COLOR_ENABLE_CHANGED)
        )
    }

    private fun unregisterBroadcasts() {
        mBroadcastReceiver?.let {
            unregisterReceiver(it)
            mBroadcastReceiver = null
        }
    }

    open fun getContainerId(): Int {
        return 0
    }

    open fun getCurrentFragment(): Fragment? {
        return mNavigator.getCurrentFragment()
    }

    open fun getNavigator(): FragmentNavigator {
        return mNavigator
    }

    override fun onFragmentStackChanged(fm: FragmentManager, fragment: Fragment) {

    }

    fun pushFragment(fragment: Fragment, tag: String?, anim: Boolean, hideOther: Boolean = true) {
        if (anim) {
            pushFragment(
                fragment,
                tag,
                Pair(R.anim.fade_in, R.anim.fade_out),
                hideOther
            )
        } else {
            pushFragment(
                fragment,
                tag,
                Pair(R.anim.do_nothing, R.anim.do_nothing),
                hideOther
            )
//            pushFragment(fragment, tag, null, hideOther)
        }
    }

    fun pushFragment(
        fragment: Fragment,
        tag: String?,
        anim: Pair<Int, Int>?,
        hideOther: Boolean = true
    ) {
        getNavigator().pushFragment(getContainerId(), fragment, tag, anim, hideOther)
    }

    fun pushClearTopFragment(fragment: Fragment, tag: String, anim: Boolean) {
        val pair = if (anim) {
            Pair(R.anim.enter_from_right, R.anim.exit_to_left)
        } else {
            null
        }
        pushClearTopFragment(fragment, tag, pair)
    }

    fun pushClearTopFragment(fragment: Fragment, tag: String, anim: Pair<Int, Int>?) {
        getNavigator().pushClearTopFragment(getContainerId(), Pair(fragment, tag), anim)
    }

    fun popFragment(anim: Boolean) {
        if (anim) {
//            popFragment(Pair(R.anim.enter_from_left, R.anim.exit_to_right))
            popFragment(Pair(R.anim.fade_in, R.anim.fade_out))
        } else {
            popFragment(null)
        }
    }

    fun pushThenPopCurrentFragment(fragment: Fragment, tag: String, anim: Boolean) {
        if (anim) {
//            popFragment(Pair(R.anim.enter_from_left, R.anim.exit_to_right))
            getNavigator().pushThenPopCurrentFragment(
                getContainerId(),
                Pair(fragment, tag),
                Pair(R.anim.fade_in, R.anim.fade_out)
            )
        } else {
            getNavigator().pushThenPopCurrentFragment(
                getContainerId(),
                Pair(fragment, tag),
                Pair(R.anim.do_nothing, R.anim.do_nothing),
            )
        }
    }

    fun popFragment(anim: Pair<Int, Int>?) {
        getNavigator().popFragment(anim)
    }

    fun isBackPressed(): Boolean {
        return mIsBackPressed
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        mPermissionsHelper?.onRequestPermissionsResult(
//            this,
//            requestCode,
//            permissions,
//            grantResults
//        )
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        DebugLogger.d(TAG, "onBackPressed: $this")
//        if ((mRootLayout.getTopView() as? OnBackPressedListener)?.onBackPressed() == true) return
//        if ((getCurrentFragment() as? OnBackPressedListener)?.onBackPressed() == true) return
//
//        this.mIsBackPressed = true
//        popFragment(true)
//    }

    open fun init(savedInstanceState: Bundle?) {
        applyTheme()
        setStatusBarForegroundMode(false, isFullLayout)
//        lifecycleScope.launch {
//            getViewModel()?.backgroundState?.onEach { uiModel ->
//                mRootLayout[0].setBackgroundResource(uiModel.backgroundResId ?: 0)
//            }?.launchIn(this)
//        }
        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            DebugLogger.d(TAG, "onAttachFragment: ${this@BaseActivity}, fragment = $fragment")
        }
        registerBroadcasts()
    }

    fun applyTheme() {
//        Log.d(
//            TAG,
//            "applyTheme: backgroundColorsEnabled=${getViewModel()?.isBackgroundColorsEnabled()}, this=$this"
//        )
//        if (getViewModel()?.isBackgroundColorsEnabled() == true) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
    }

    fun setStatusBarBackgroundColor(@ColorRes colorRes: Int?) {
        if (colorRes == null) return
        setStatusBarBackgroundColorInt(ContextCompat.getColor(this, colorRes))
    }

    @Suppress("DEPRECATION")
    fun setStatusBarBackgroundColorInt(colorInt: Int) {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = colorInt
    }

    @Suppress("DEPRECATION")
    fun setStatusBarForegroundMode(night: Boolean, fullLayout: Boolean?) {
        val decorView = window?.decorView
        if (night) {
            if (fullLayout == true) {
                decorView?.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        } else {
            if (fullLayout == true) {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        decorView?.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }

                    Configuration.UI_MODE_NIGHT_NO -> {
                        decorView?.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                }

            } else {
                decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun showError(
        message: String?,
        action: String?,
        actionSelected: ((OneOptionView) -> Unit)?,
        backPressed: ((OneOptionView) -> Unit)?,
    ) {

    }

    override fun showToast(message: String?, duration: Int) {
        Toast.makeText(this, message, duration).show()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {
    }

    fun showKeyboard() {
        (currentFocus as? EditText)?.showKeyboard()
    }

    fun hideKeyboard() {
        (currentFocus as? EditText)?.hideKeyboard()
    }
}