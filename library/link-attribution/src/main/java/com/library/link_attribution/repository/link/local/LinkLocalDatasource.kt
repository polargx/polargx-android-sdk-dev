package com.library.link_attribution.repository.link.local

import com.library.link_attribution.repository.link.local.model.LinkEntity

interface LinkLocalDatasource {

    fun getLink(): LinkEntity?
    fun setLink(link: LinkEntity?)

}
