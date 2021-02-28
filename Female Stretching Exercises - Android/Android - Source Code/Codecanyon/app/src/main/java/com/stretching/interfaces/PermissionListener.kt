package com.stretching.interfaces

interface PermissionListener {

    fun onPermissionClick()

    fun onPermissionAllow(isAllow: Boolean)
}