package com.jdev.wandroid.mockdata

import java.io.Serializable

class ItemVo : Serializable {
    var title: String? = null
    var desc: String? = null
    var actName: String? = null
    var fragName: String? = null
    var level: LEVEL? = LEVEL.NOPE
    var state: STATE? = STATE.TODO
}
 