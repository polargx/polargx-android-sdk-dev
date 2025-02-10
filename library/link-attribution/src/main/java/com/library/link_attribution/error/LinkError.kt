package com.library.link_attribution.error


sealed class LinkError : Throwable() {

    data class Init(override val message: String?) : LinkError()
    data class Configuration(override val message: String?) : LinkError()
    data class Link(override val message: String?) : LinkError()
}