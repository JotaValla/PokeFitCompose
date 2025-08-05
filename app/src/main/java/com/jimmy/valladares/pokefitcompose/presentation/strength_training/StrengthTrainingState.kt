package com.jimmy.valladares.pokefitcompose.presentation.strength_training

data class StrengthTrainingState(
    val timerValue: String = "00:00",
    val timerSeconds: Int = 0,
    val isPaused: Boolean = true,
    val isTrainingStarted: Boolean = false,
    val selectedExercise: String = "Item 1",
    val availableExercises: List<String> = listOf(
        "Item 1", "Item 2", "Item 3", 
        "Press de banca", "Sentadillas", "Peso muerto",
        "Curl de bíceps", "Press militar", "Remo con barra"
    ),
    val exerciseRows: List<ExerciseRow> = getInitialRows(),
    val exerciseHistory: Map<String, List<ExerciseRow>> = emptyMap(), // Historial de ejercicios completados
    val isFinishVisible: Boolean = false,
    val showPikachuRunning: Boolean = false,
    val completedExercises: List<String> = emptyList() // Lista de ejercicios completados
)

sealed class StrengthTrainingAction {
    data class SelectExercise(val exercise: String) : StrengthTrainingAction()
    object StartTraining : StrengthTrainingAction()
    object PauseResumeTimer : StrengthTrainingAction()
    object AddSet : StrengthTrainingAction()
    data class ToggleSetComplete(val index: Int) : StrengthTrainingAction()
    data class UpdateWeight(val index: Int, val weight: Int) : StrengthTrainingAction()
    data class UpdateReps(val index: Int, val reps: Int) : StrengthTrainingAction()
    object FinishTraining : StrengthTrainingAction()
    object TimerTick : StrengthTrainingAction()
    object SwitchExercise : StrengthTrainingAction() // Nueva acción para cambiar ejercicio
}

sealed class StrengthTrainingEvent {
    object TrainingCompleted : StrengthTrainingEvent()
    data class ShowMessage(val message: String) : StrengthTrainingEvent()
}

data class ExerciseRow(
    val previous: String,
    val set: Int,
    val weight: Int,
    val reps: Int,
    val isCompleted: Boolean = false
)

private fun getInitialRows(): List<ExerciseRow> {
    return listOf(
        ExerciseRow("8 x 12kg", 1, 12, 8),
        ExerciseRow("8 x 12kg", 2, 12, 8),
        ExerciseRow("8 x 12kg", 3, 12, 8)
    )
}

fun createNewRow(setNumber: Int): ExerciseRow {
    return ExerciseRow("8 x 12kg", setNumber, 12, 8)
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
