package com.library.link_attribution.di

import com.library.link_attribution.repository.configuration.ConfigurationRepository
import com.library.link_attribution.repository.configuration.ConfigurationRepositoryImpl
import com.library.link_attribution.repository.configuration.local.ConfigurationLocalDatasource
import com.library.link_attribution.repository.configuration.local.ConfigurationLocalDatasourceImpl
import com.library.link_attribution.repository.configuration.remote.ConfigurationRemoteDatasource
import com.library.link_attribution.repository.configuration.remote.ConfigurationRemoteDatasourceImpl
import com.library.link_attribution.repository.link.LinkRepository
import com.library.link_attribution.repository.link.LinkRepositoryImpl
import com.library.link_attribution.repository.link.local.LinkLocalDatasource
import com.library.link_attribution.repository.link.local.LinkLocalDatasourceImpl
import com.library.link_attribution.repository.link.remote.LinkRemoteDatasource
import com.library.link_attribution.repository.link.remote.LinkRemoteDatasourceImpl
import com.library.link_attribution.repository.tracking.TrackingRepository
import com.library.link_attribution.repository.tracking.TrackingRepositoryImpl
import com.library.link_attribution.repository.tracking.local.TrackingLocalDatasource
import com.library.link_attribution.repository.tracking.local.TrackingLocalDatasourceImpl
import com.library.link_attribution.repository.tracking.remote.TrackingRemoteDatasource
import com.library.link_attribution.repository.tracking.remote.TrackingRemoteDatasourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val linkAttributeModule = module {
    // Repositories

    singleOf(::ConfigurationLocalDatasourceImpl) { bind<ConfigurationLocalDatasource>() }
    singleOf(::ConfigurationRemoteDatasourceImpl) { bind<ConfigurationRemoteDatasource>() }
    singleOf(::ConfigurationRepositoryImpl) { bind<ConfigurationRepository>() }

    singleOf(::LinkLocalDatasourceImpl) { bind<LinkLocalDatasource>() }
    singleOf(::LinkRemoteDatasourceImpl) { bind<LinkRemoteDatasource>() }
    singleOf(::LinkRepositoryImpl) { bind<LinkRepository>() }

    singleOf(::TrackingLocalDatasourceImpl) { bind<TrackingLocalDatasource>() }
    singleOf(::TrackingRemoteDatasourceImpl) { bind<TrackingRemoteDatasource>() }
    singleOf(::TrackingRepositoryImpl) { bind<TrackingRepository>() }

}