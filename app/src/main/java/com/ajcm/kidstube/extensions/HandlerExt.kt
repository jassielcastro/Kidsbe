package com.ajcm.kidstube.extensions

import android.os.Handler

fun delay(time: Long, completion: () -> Unit) {
    Handler().postDelayed({
        completion()
    }, time)
}