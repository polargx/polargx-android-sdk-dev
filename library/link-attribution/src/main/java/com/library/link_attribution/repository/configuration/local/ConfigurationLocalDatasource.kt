package com.library.link_attribution.repository.configuration.local

import com.library.link_attribution.repository.configuration.local.model.ConfigurationEntity
import com.library.link_attribution.repository.configuration.local.model.InitSessionEntity

interface ConfigurationLocalDatasource {

    fun getInitSession(): InitSessionEntity?
    fun setInitSession(initSession: InitSessionEntity?)

    fun getConfiguration(): ConfigurationEntity?
    fun setConfiguration(configuration: ConfigurationEntity?)

}
