package com.stretching.utils

import com.stretching.objects.Spinner
import java.util.*


interface SpinnerCallback {
    abstract fun onDone(list: ArrayList<Spinner>)
}