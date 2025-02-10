package com.library.core.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.app.shared.logger.DebugLogger
import com.library.core.activity.BaseActivity
import com.library.core.activity.listener.OnBackPressedListener
import com.library.core.activity.listener.OnDispatchTouchEventListener
import com.library.core.extension.getTopView
import com.library.core.extension.hideKeyboard
import com.library.core.fragment.navigator.FragmentNavigator
import com.library.core.view.popup.OneOptionView

abstract class BaseFragment(
    contentLayoutId: Int
) : Fragment(contentLayoutId),
    OnDispatchTouchEventListener,
    OnBackPressedListener,
    BaseFragmentViewModel.LiveEvent {

    var mName = ">>>BaseFragment"
    var mId = "1"

    var mActivity: BaseActivity? = null
    private val mNavigator by lazy { FragmentNavigator(activity, childFragmentManager) }

//    @Inject
//    lateinit var mViewModelFactory: ViewModelProviderFactory

    fun getName(): String {
        return "$mName#$mId"
    }

//    abstract fun performDependencyInjection()
//    abstract fun getViewModel(): BaseFragmentViewModel

    open fun getFragmentNavigator(): FragmentNavigator {
        return mNavigator
    }

    open fun getContainerId(): Int {
        return 0
    }

    fun pushFragment(fragment: Fragment, tag: String?, anim: Boolean, hideOther: Boolean = true) {
        mActivity?.pushFragment(fragment, tag, anim, hideOther)
    }

    fun pushFragment(
        fragment: Fragment,
        tag: String?,
        anim: Pair<Int, Int>?,
        hideOther: Boolean = true
    ) {
        getFragmentNavigator().pushFragment(getContainerId(), fragment, tag, anim, hideOther)
    }

    fun pushClearTopFragment(fragment: Fragment, tag: String, anim: Boolean) {
        mActivity?.pushClearTopFragment(fragment, tag, anim)
    }

    fun popFragment(anim: Boolean = true) {
        mActivity?.popFragment(anim)
    }

    fun popFragment(anim: Pair<Int, Int>?) {
        getFragmentNavigator().popFragment(anim)
    }

    open fun getRootView(): View? {
        return null
    }

    open fun getAnimContentView(): View? {
        return null
    }

    abstract fun getViewModel(): BaseFragmentViewModel<out BaseFragmentViewModel.LiveEvent>?

    override fun onAttachFragment(childFragment: Fragment) {
        DebugLogger.d(getName(), "onAttachFragment: childFragment => $childFragment")
        super.onAttachFragment(childFragment)
    }

    override fun onAttach(context: Context) {
        DebugLogger.d(getName(), "onAttach: context => $context")
        super.onAttach(context)
        if (context is BaseActivity) {
            mActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DebugLogger.d(getName(), "onCreate: savedInstanceState => $savedInstanceState")
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        DebugLogger.d(getName(), "onGetLayoutInflater: savedInstanceState => $savedInstanceState")
        return super.onGetLayoutInflater(savedInstanceState)
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        DebugLogger.d(
            getName(),
            "onInflate: attrs => $attrs, savedInstanceState => $savedInstanceState"
        )
        super.onInflate(context, attrs, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DebugLogger.d(
            getName(),
            "onCreateView: container => $container, savedInstanceState => $savedInstanceState"
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DebugLogger.d(
            getName(),
            "onViewCreated: view => $view, savedInstanceState => $savedInstanceState"
        )
        super.onViewCreated(view, savedInstanceState)
        init(view)
        if (activity?.supportFragmentManager?.fragments?.size == 1) {
            onCreateAnimationEnded()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        DebugLogger.d(getName(), "onActivityCreated: savedInstanceState => $savedInstanceState")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        DebugLogger.d(getName(), "onViewStateRestored: savedInstanceState => $savedInstanceState")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        DebugLogger.d(getName(), "onStart")
        super.onStart()
    }

    override fun onResume() {
        DebugLogger.d(getName(), "onResume")
        super.onResume()
        onForeground()
    }

    override
    fun onHiddenChanged(hidden: Boolean) {
        DebugLogger.d(getName(), "onHiddenChanged: hidden = $hidden")
        super.onHiddenChanged(hidden)
        if (mActivity?.supportFragmentManager == parentFragmentManager) {
            mActivity?.onFragmentStackChanged(parentFragmentManager, this)
        }
        if (hidden) {
            onBackground()
        } else {
            onForeground()
        }
    }

    override fun onPause() {
        DebugLogger.d(getName(), "onPause")
        super.onPause()
        onBackground()
    }

    override fun onStop() {
        DebugLogger.d(getName(), "onStop")
        super.onStop()
    }

    open fun onForeground() {
        DebugLogger.d(getName(), "onForeground")
        setupTopBar()
    }

    open fun onBackground() {
        DebugLogger.d(getName(), "onBackground")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        DebugLogger.d(getName(), "onSaveInstanceState: outState = $outState")
        super.onSaveInstanceState(outState)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        DebugLogger.d(
            getName(),
            "onMultiWindowModeChanged: isInMultiWindowMode = $isInMultiWindowMode"
        )
        super.onMultiWindowModeChanged(isInMultiWindowMode)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        DebugLogger.d(
            getName(),
            "onPictureInPictureModeChanged: isInPictureInPictureMode = $isInPictureInPictureMode"
        )
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        DebugLogger.d(getName(), "onConfigurationChanged: newConfig = $newConfig")
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        DebugLogger.d(
            getName(),
            "onCreateAnimation: transit = $transit, enter = $enter, nextAnim = $nextAnim"
        )
        if (nextAnim == 0) return super.onCreateAnimation(transit, enter, nextAnim)

        val anim = AnimationUtils.loadAnimation(activity, nextAnim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (getViewModel()?.isInitAnimCompleted() != true) {
                    onCreateAnimationEnded()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        return anim
    }

    override fun onLowMemory() {
        DebugLogger.d(getName(), "onLowMemory")
        super.onLowMemory()
    }

    override fun onDestroyView() {
        DebugLogger.d(getName(), "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        DebugLogger.d(getName(), "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        DebugLogger.d(getName(), "onDetach")
        super.onDetach()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        DebugLogger.d(
            getName(),
            "onRequestPermissionsResult: requestCode = $requestCode, permissions = $permissions, grantResults = $grantResults, $this"
        )
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        DebugLogger.d(
            getName(),
            "onActivityResult: requestCode = $requestCode, resultCode = $resultCode, $this"
        )
        super.onActivityResult(requestCode, resultCode, data)
    }

    open fun getCurrentFragment(): Fragment? {
        return mNavigator.getCurrentFragment()
    }

    override fun onDispatchTouchEvent(ev: MotionEvent?): Boolean {
        (activity?.currentFocus as? EditText)?.let {
            it.clearFocus()
            it.hideKeyboard()
        }
        return false
    }

    override fun onBackPressed(): Boolean {
        DebugLogger.d(getName(), "onHandleBackPressed")
        if ((getRootView()?.getTopView() as? OnBackPressedListener)?.onBackPressed() == true) return true
        if ((getCurrentFragment() as? OnBackPressedListener)?.onBackPressed() == true) return true
        return false
    }

    open fun init(view: View) {
//        performDependencyInjection()
        getAnimContentView()?.visibility = View.INVISIBLE
//        mActivity?.setStatusBarBackgroundColor(R.color.statusBarColor)
        if (mActivity?.supportFragmentManager == parentFragmentManager) {
            mActivity?.onFragmentStackChanged(parentFragmentManager, this)
        }
        setupTopBar()
    }

    open fun setupTopBar() {

    }

    override fun showLoading() {
        mActivity?.showLoading()
    }

    override fun hideLoading() {
        mActivity?.hideLoading()
    }

    override fun showError(
        message: String?,
        action: String?,
        actionSelected: ((OneOptionView) -> Unit)?,
        backPressed: ((OneOptionView) -> Unit)?,
    ) {
        mActivity?.showError(
            message = message,
            action = action,
            actionSelected = null,
            backPressed = null
        )
    }

    override fun showToast(message: String?, duration: Int) {
        mActivity?.showToast(message, duration)
    }

    fun showKeyboard() {
        mActivity?.showKeyboard()
    }

    fun hideKeyboard() {
        mActivity?.hideKeyboard()
    }


    open fun onCreateAnimationEnded() {

    }

}