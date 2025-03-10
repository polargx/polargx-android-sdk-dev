package com.library.polar_gx.configuration

import com.library.polar_gx.Polar

open class EnvConfiguration(
    open val name: String,
    open val server: String,
    open val supportedBaseDomains: String
)

class DevEnvConfiguration(
    override val name: String = "Development",
    override val server: String = "lydxigat68.execute-api.us-east-1.amazonaws.com/dev",
    override val supportedBaseDomains: String = ".makelabs.ai"
) : EnvConfiguration(name, server, supportedBaseDomains)

class ProdEnvConfiguration(
    override val name: String = "Production",
    override val server: String = "lydxigat68.execute-api.us-east-1.amazonaws.com/prod",
    override val supportedBaseDomains: String = ".gxlnk.com"
) : EnvConfiguration(name, server, supportedBaseDomains)

object Configuration {
    val Brand: String = "Polar"

    val Env: EnvConfiguration = if (Polar.isDevelopmentEnabled) {
        DevEnvConfiguration()
    } else {
        ProdEnvConfiguration()
    }
}