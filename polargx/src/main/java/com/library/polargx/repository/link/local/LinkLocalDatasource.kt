package com.library.polargx.repository.link.local

import com.library.polargx.repository.link.local.model.link.LinkDataEntity

interface LinkLocalDatasource {

    fun getLinkData(): LinkDataEntity?
    fun setLinkData(link: LinkDataEntity?)

}
