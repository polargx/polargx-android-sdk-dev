package com.library.link_attribution.model.extension.json

import org.json.JSONObject

fun JSONObject.getEvenNonExisted(name: String?): Any? {
    return if (name == null || !has(name) || isNull(name)) {
        null
    } else {
        get(name)
    }
}