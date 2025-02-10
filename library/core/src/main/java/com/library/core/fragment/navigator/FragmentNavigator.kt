package com.library.core.fragment.navigator

import android.app.Activity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.shared.logger.DebugLogger

class FragmentNavigator(
    val activity: Activity?,
    private var mFragmentManager: FragmentManager?
) {

    companion object {
        const val TAG = "BaseNavigator"
    }

    private val mTagList = ArrayList<String>()
    private var mOnPopFragmentListener: OnPopFragmentListener? = null

    fun getTagList(): ArrayList<String> {
        return mTagList
    }


    fun setTagList(tagList: ArrayList<String>?) {
        this.mTagList.clear()
        tagList?.let {
            this.mTagList.addAll(tagList)
        }
    }

    fun getFragment(tag: String?): Fragment? {
        return if (mTagList.isEmpty()) {
            null
        } else {
            mFragmentManager?.findFragmentByTag(tag)
        }
    }

    fun getCurrentFragment(): Fragment? {
        return if (mTagList.isEmpty()) {
            null
        } else {
            getFragment(mTagList.last())
        }
    }

    fun setOnPopLastFragmentListener(onPopFragmentListener: OnPopFragmentListener?) {
        this.mOnPopFragmentListener = onPopFragmentListener
    }

    fun pushFragment(
        @IdRes containerId: Int,
        fragment: Fragment,
        tag: String?,
        anim: Pair<Int, Int>?,
        hideOthers: Boolean = true
    ) {
        mFragmentManager?.let { fm ->
            if (fm.findFragmentByTag(tag) == null) {
                fm.beginTransaction().let { ft ->
                    anim?.let { anim ->
                        ft.setCustomAnimations(anim.first, anim.second)
                    }
                    ft.add(containerId, fragment, tag)
                    ft.setPrimaryNavigationFragment(fragment)
                    mTagList.add(tag ?: "")

                    if (hideOthers) {
                        fm.findFragmentById(containerId)?.let { showingFragment ->
                            ft.hide(showingFragment)
                        }
                    }
                    ft.commitAllowingStateLoss()
                }
            } else {
                DebugLogger.d(TAG, "pushFragment: FAILED => fragment does existed, tag => $tag")
            }
        }
    }

    fun pushThenPopCurrentFragment(
        containerId: Int,
        pair: Pair<Fragment, String>,
        anim: Pair<Int, Int>?
    ) {
        mFragmentManager?.let { fm ->
            fm.beginTransaction().let { ft ->
                anim?.let { anim ->
                    ft.setCustomAnimations(anim.first, anim.second)
                }
                mTagList.lastOrNull()?.let {tag->
                    fm.findFragmentByTag(tag)?.let { removeFragment ->
                        ft.remove(removeFragment)
                    }
                    mTagList.removeLastOrNull()
                }
                ft.add(containerId, pair.first, pair.second)
                ft.setPrimaryNavigationFragment(pair.first)
                mTagList.add(pair.second)
                ft.commitAllowingStateLoss()
            }
        }
    }

    fun pushClearTopFragment(
        containerId: Int,
        pair: Pair<Fragment, String>,
        anim: Pair<Int, Int>?
    ) {
        mFragmentManager?.let { fm ->
            fm.beginTransaction().let { ft ->
                anim?.let { anim ->
                    ft.setCustomAnimations(anim.first, anim.second)
                }

                for (tag in mTagList) {
                    fm.findFragmentByTag(tag)?.let { removeFragment ->
                        ft.remove(removeFragment)
                    }
                }
                mTagList.clear()
                ft.add(containerId, pair.first, pair.second)
                ft.setPrimaryNavigationFragment(pair.first)
                mTagList.add(pair.second)
                ft.commitAllowingStateLoss()
            }
        }
    }


    fun popFragment(anim: Pair<Int, Int>?) {
        val removeFragment = getCurrentFragment()
        if (removeFragment == null) {
            if (mOnPopFragmentListener?.onPopEmptyFragment(this) != true) {
                activity?.finish()
            }
        } else {
            if (getTagList().size == 1) {
                if (mOnPopFragmentListener?.onPopLastFragment(this, removeFragment) != true) {
                    activity?.finish()
                }
            } else {
                mFragmentManager?.let { fm ->
                    fm.beginTransaction().let { ft ->
                        anim?.let { anim ->
                            ft.setCustomAnimations(anim.first, anim.second)
                        }
                        ft.remove(removeFragment)
                        mTagList.remove(removeFragment.tag)

                        fm.findFragmentByTag(mTagList.last())?.let { previousFragment ->
                            ft.show(previousFragment)
                            ft.setPrimaryNavigationFragment(previousFragment)
                        }
                        ft.commitAllowingStateLoss()
                    }
                }
            }
        }
    }


    fun showFragment(fragment: Fragment?) {
        mFragmentManager?.let { fm ->
            fm.findFragmentByTag(fragment?.tag)?.let { displayFragment ->
                fm.beginTransaction()
                    .show(displayFragment)
                    .commitAllowingStateLoss()
            }
        }
    }


    fun showClearTopFragment(
        pair: Pair<Fragment, String>,
        anim: Pair<Int, Int>?
    ) {
        mFragmentManager?.let { fm ->
            fm.findFragmentByTag(pair.second)?.let { displayFragment ->
                fm.beginTransaction().let { ft ->
                    anim?.let { anim ->
                        ft.setCustomAnimations(anim.first, anim.second)
                    }

                    for (i in mTagList.size downTo mTagList.indexOf(pair.second)) {
                        fm.findFragmentByTag(mTagList[i])?.let { removeFragment ->
                            ft.remove(removeFragment)
                            mTagList.remove(removeFragment.tag)
                        }
                    }

                    ft.show(displayFragment)
                    ft.setPrimaryNavigationFragment(displayFragment)
                    ft.commitAllowingStateLoss()
                }
            }
        }
    }


    fun hideFragment(fragment: Fragment?) {
        mFragmentManager?.let { fm ->
            fm.findFragmentByTag(fragment?.tag)?.let { hideFragment ->
                fm.beginTransaction()
                    .hide(hideFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    fun clear() {
        mFragmentManager?.let { fm ->
            fm.beginTransaction().let { ft ->
                for (tag in mTagList) {
                    fm.findFragmentByTag(tag)?.let { removeFragment ->
                        ft.remove(removeFragment)
                    }
                }
                mTagList.clear()
                ft.commitAllowingStateLoss()
            }
        }
    }

    interface OnPopFragmentListener {
        fun onPopEmptyFragment(navigator: FragmentNavigator): Boolean
        fun onPopLastFragment(navigator: FragmentNavigator, lastFragment: Fragment?): Boolean
    }
}