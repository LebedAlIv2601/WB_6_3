package com.example.wb_6_3.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private val _isPICounting = MutableLiveData<Boolean>()
    val isPICounting: LiveData<Boolean>
        get() = _isPICounting

    private val _isReset = MutableLiveData<Boolean>()
    val isReset: LiveData<Boolean>
        get() = _isReset

    private val _pi = MutableLiveData<String>()
    val pi: LiveData<String>
        get() = _pi

    private val _numberOfIterations = MutableLiveData<Int>()
    val numberOfIterations: LiveData<Int>
        get() = _numberOfIterations

    init {
        _isPICounting.value = false
        _isReset.value = false
        _pi.value = "0"
        _numberOfIterations.value = 0
    }

    fun setIsPICounting(b: Boolean){
        _isPICounting.value = b
    }

    fun setPi(number: String){
        _pi.value = number
    }

    fun setNumberOfIterations(digit: Int){
        _numberOfIterations.value = digit
    }

    fun setIsReset(b: Boolean){
        _isReset.value = b
    }
}