package de.repeatuntil.yata.domain.common

sealed class DomainException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)

    class RepositoryException(throwable: Throwable) : DomainException("Repository exception", throwable)
}
