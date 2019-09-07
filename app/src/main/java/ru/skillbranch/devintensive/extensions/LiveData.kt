package ru.skillbranch.devintensive.extensions

import androidx.lifecycle.MutableLiveData

fun <T> mutableLiveData(defaultValue: T): MutableLiveData<T> = MutableLiveData<T>().also { it.value = defaultValue }