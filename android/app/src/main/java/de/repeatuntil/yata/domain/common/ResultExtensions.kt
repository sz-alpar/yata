package de.repeatuntil.yata.domain.common

fun <R> Result<R>.mapFailureToRepositoryException(): Result<R> {
    return fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(DomainException.RepositoryException(it)) }
    )
}