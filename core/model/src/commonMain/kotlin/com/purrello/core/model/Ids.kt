package com.purrello.core.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

// Typed ids — never pass raw Strings across layers. @Serializable so they can be route arguments.

@Serializable
@JvmInline
value class PetId(val value: String)

@Serializable
@JvmInline
value class UserId(val value: String)

@Serializable
@JvmInline
value class VaccinationId(val value: String)

@Serializable
@JvmInline
value class DocumentId(val value: String)

@Serializable
@JvmInline
value class CareRecordId(val value: String)

@Serializable
@JvmInline
value class LostAlertId(val value: String)
