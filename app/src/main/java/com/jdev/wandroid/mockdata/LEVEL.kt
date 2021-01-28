package com.jdev.wandroid.mockdata

enum class LEVEL(name: String? = "nope") {
    LEVEL_CRITICAL("critical"),
    LEVEL_HIGH("high"),
    LEVEL_MIDDLE("middle"),
    LEVEL_LOW("low"),
    LEVEL_NOPE("nope"),
}

enum class STATE(name: String? = "todo") {
    COMPLETED("completed"),
    SUSPEND("suspend"),
    ONGOING("ongoing"),
    TODO("todo"),
}