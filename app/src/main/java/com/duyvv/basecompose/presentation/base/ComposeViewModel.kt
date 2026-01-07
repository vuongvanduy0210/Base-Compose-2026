package com.duyvv.basecompose.presentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class ComposeViewModel<S : UiState, I : MVIIntent, F : MVIEffect> : ViewModel() {

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(createInitialState()) }
    val uiState: StateFlow<S>
        get() = _uiState.asStateFlow()
    val uiStateValue: S
        get() = _uiState.value

    abstract fun createInitialState(): S

    private val _effect: Channel<F> = Channel()
    val effect = _effect.receiveAsFlow()

    abstract fun sendIntent(intent: I)

    protected fun updateUiState(reduce: S.() -> S) {
        _uiState.update { it.reduce() }
    }

    fun sendEffect(builder: () -> F) {
        viewModelScope.launch { _effect.send(builder()) }
    }

    open fun handleException(throwable: Throwable) {
        Log.e("BaseViewModel", "Exception handled: ${throwable.message}")
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    protected val viewModelSafetyScope by lazy {
        viewModelScope + exceptionHandler
    }
}