package com.library.core.activity.listener

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface OnFragmentStackChangedListener {
    fun onFragmentStackChanged(fm: FragmentManager, fragment: Fragment)
}