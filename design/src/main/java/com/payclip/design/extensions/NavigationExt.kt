package com.payclip.design.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

fun Fragment.navigateTo(@IdRes idAction: Int, bundle: Bundle? = null, navOptions: NavOptions? = null, extras: Navigator.Extras? = null) {
    findNavController().navigate(idAction, bundle, navOptions, extras)
}