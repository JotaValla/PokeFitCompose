package com.jimmy.valladares.pokefitcompose.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(WelcomeScreenState())
    val state: StateFlow<WelcomeScreenState> = _state.asStateFlow()
    
    private val _events = Channel<WelcomeScreenEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: WelcomeScreenAction) {
        when (action) {
            is WelcomeScreenAction.OnGetStarted -> {
                handleGetStarted()
            }
        }
    }
    
    private fun handleGetStarted() {
        viewModelScope.launch {
            _events.send(WelcomeScreenEvent.NavigateToLogin)
        }
    }
}
