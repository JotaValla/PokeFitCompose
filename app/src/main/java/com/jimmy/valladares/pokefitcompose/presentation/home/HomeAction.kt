package com.jimmy.valladares.pokefitcompose.presentation.home

sealed interface HomeAction {
    data object LoadUserProfile : HomeAction
    data object RefreshData : HomeAction
    data object StartTraining : HomeAction
    data class NavigateToTab(val tab: BottomNavItem) : HomeAction
    data class RecordTraining(val expGained: Int) : HomeAction
    data object UpdateLevel : HomeAction
    data object ClearError : HomeAction
}

enum class BottomNavItem(
    val iconRes: String, // Usaremos nombres de archivos para ahora
    val selectedIconRes: String? = null,
    val label: String,
    val route: String
) {
    HOME(
        iconRes = "icon_home",
        label = "Home",
        route = "home"
    ),
    STATS(
        iconRes = "icon_stats", 
        label = "Stats",
        route = "stats"
    ),
    TRAINING(
        iconRes = "icon_train_fuerza",
        label = "Entrenar",
        route = "training"
    ),
    PROFILE(
        iconRes = "icon_perfil",
        label = "Perfil", 
        route = "profile"
    )
}
