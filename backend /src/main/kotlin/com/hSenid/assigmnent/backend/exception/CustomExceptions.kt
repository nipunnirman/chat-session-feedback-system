package com.hSenid.assigmnent.backend.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)
class AlreadyRespondedException(message: String) : RuntimeException(message)
class FeedbackExpiredException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
