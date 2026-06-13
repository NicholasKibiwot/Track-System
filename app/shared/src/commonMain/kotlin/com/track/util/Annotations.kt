package com.track.util

@Target(AnnotationTarget.CLASS)
expect annotation class CommonHiltViewModel()

@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
expect annotation class CommonInject()

@Target(AnnotationTarget.CLASS)
expect annotation class CommonSingleton()

