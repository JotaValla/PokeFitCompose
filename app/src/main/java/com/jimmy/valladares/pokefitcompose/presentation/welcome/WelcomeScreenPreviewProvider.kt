package com.jimmy.valladares.pokefitcompose.presentation.welcome

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class WelcomeScreenStatePreviewProvider : PreviewParameterProvider<WelcomeScreenState> {
    override val values = sequenceOf(
        WelcomeScreenState(
            isLoading = false,
            error = null
        ),
        WelcomeScreenState(
            isLoading = true,
            error = null
        ),
        WelcomeScreenState(
            isLoading = false,
            error = "Something went wrong"
        )
    )
}
