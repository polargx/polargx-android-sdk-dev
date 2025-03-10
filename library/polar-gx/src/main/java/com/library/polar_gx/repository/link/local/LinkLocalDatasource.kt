package com.library.polar_gx.repository.link.local

import com.library.polar_gx.repository.link.local.model.link.LinkDataEntity

interface LinkLocalDatasource {

    fun getLinkData(): LinkDataEntity?
    fun setLinkData(link: LinkDataEntity?)

}
