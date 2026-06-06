package com.tech.multitypeview.ui

sealed class MultiTypeSource {
    object Primary : MultiTypeSource()
    object Secondary : MultiTypeSource()
}
