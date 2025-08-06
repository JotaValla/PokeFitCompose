package com.jimmy.valladares.pokefitcompose.presentation.strength_training

data class StrengthTrainingState(
    val timerValue: String = "00:00",
    val timerSeconds: Int = 0,
    val isPaused: Boolean = true,
    val isTrainingStarted: Boolean = false,
    val selectedExercise: String = "Press de banca",
    val availableExercises: List<String> = listOf(
        "Press de banca", "Sentadillas", "Peso muerto",
        "Curl de bíceps", "Press militar", "Remo con barra",
        "Dominadas", "Fondos", "Zancadas", "Press de hombros"
    ),
    val selectedExercises: List<String> = emptyList(), // Lista de ejercicios seleccionados para el entrenamiento
    val currentExerciseIndex: Int = 0, // Índice del ejercicio actual durante el entrenamiento
    val exerciseRows: List<ExerciseRow> = getInitialRows(),
    val exerciseHistory: Map<String, List<ExerciseRow>> = emptyMap(), // Historial de ejercicios completados
    val isFinishVisible: Boolean = false,
    val showPikachuRunning: Boolean = false,
    val completedExercises: List<String> = emptyList(), // Lista de ejercicios completados
    // Nuevos campos para el temporizador de descanso
    val isRestTimerActive: Boolean = false,
    val restTimeSeconds: Int = 0,
    val restTimeValue: String = "00:00",
    val defaultRestTime: Int = 90, // 90 segundos por defecto
    val showRestTimer: Boolean = false
)

sealed class StrengthTrainingAction {
    data class SelectExercise(val exercise: String) : StrengthTrainingAction()
    object AddExerciseToWorkout : StrengthTrainingAction() // Agregar ejercicio actual a la lista
    data class RemoveExerciseFromWorkout(val exercise: String) : StrengthTrainingAction() // Remover ejercicio de la lista
    data class ChangeCurrentExercise(val exerciseIndex: Int) : StrengthTrainingAction() // Cambiar ejercicio actual durante entrenamiento
    object StartTraining : StrengthTrainingAction()
    object PauseResumeTimer : StrengthTrainingAction()
    object AddSet : StrengthTrainingAction()
    data class ToggleSetComplete(val index: Int) : StrengthTrainingAction()
    data class UpdateWeight(val index: Int, val weight: Int) : StrengthTrainingAction()
    data class UpdateReps(val index: Int, val reps: Int) : StrengthTrainingAction()
    object FinishTraining : StrengthTrainingAction()
    object TimerTick : StrengthTrainingAction()
    object SwitchExercise : StrengthTrainingAction() // Nueva acción para cambiar ejercicio
    // Nuevas acciones para el temporizador de descanso
    object StartRestTimer : StrengthTrainingAction()
    object RestTimerTick : StrengthTrainingAction()
    object SkipRestTimer : StrengthTrainingAction()
    object AddRestTime : StrengthTrainingAction() // Añadir 30 segundos más
    object SubtractRestTime : StrengthTrainingAction() // Restar 30 segundos
    data class SetRestTime(val seconds: Int) : StrengthTrainingAction()
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
