package com.library.link_attribution.repository.link.local

import com.library.link_attribution.repository.link.local.model.link.LinkDataEntity

interface LinkLocalDatasource {

    fun getLinkData(): LinkDataEntity?
    fun setLinkData(link: LinkDataEntity?)

}
