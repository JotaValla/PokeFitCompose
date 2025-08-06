package com.jimmy.valladares.pokefitcompose.data.result

sealed class FirestoreResult<T> {
    data class Success<T>(val data: T) : FirestoreResult<T>()
    data class Error<T>(val message: String) : FirestoreResult<T>()
}
