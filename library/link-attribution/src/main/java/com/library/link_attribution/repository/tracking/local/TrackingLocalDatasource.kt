package com.library.link_attribution.repository.tracking.local

import com.library.link_attribution.repository.tracking.local.model.TrackClickEntity

interface TrackingLocalDatasource {

    fun getTrackClick(): TrackClickEntity?
    fun setTrackClick(link: TrackClickEntity?)

}
