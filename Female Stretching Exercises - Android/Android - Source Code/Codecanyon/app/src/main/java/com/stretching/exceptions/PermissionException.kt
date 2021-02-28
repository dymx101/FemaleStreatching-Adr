package com.stretching.exceptions

import com.stretching.utils.Debug

class PermissionException : BaseException() {

    override fun printStackTrace() {
        super.printStackTrace()
        Debug.e("Permission","Permission denied" )
    }
}