package de.repeatuntil.yata.domain.common

import java.util.UUID

@JvmInline
value class Id(private val id: String = UUID.randomUUID().toString())