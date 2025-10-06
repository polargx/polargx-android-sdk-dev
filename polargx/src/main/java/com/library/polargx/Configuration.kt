package com.library.polargx

open class EnvConfiguration(
    open val name: String,
    open val server: String,
    open val supportedBaseDomains: String
)

class DevEnvConfiguration(
    override val name: String = "Development",
    override val server: String = "8mr6rftgmb.execute-api.us-east-1.amazonaws.com/dev",
    override val supportedBaseDomains: String = ".biglittlecookies.com"
) : EnvConfiguration(name, server, supportedBaseDomains)

class ProdEnvConfiguration(
    override val name: String = "Production",
    override val server: String = "8mr6rftgmb.execute-api.us-east-1.amazonaws.com/prod",
    override val supportedBaseDomains: String = ".gxlnk.com"
) : EnvConfiguration(name, server, supportedBaseDomains)

object Configuration {
    const val BRAND = "Polar"
    var Env: EnvConfiguration = ProdEnvConfiguration()
}
