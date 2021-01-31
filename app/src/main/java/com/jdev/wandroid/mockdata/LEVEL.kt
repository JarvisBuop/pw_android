package com.jdev.wandroid.mockdata

enum class LEVEL(name: String? = "nope") {
    CRITICAL("critical"),
    HIGH("high"),
    MIDDLE("middle"),
    LOW("low"),
    NOPE("nope"),
}

enum class STATE(name: String? = "todo") {
    COMPLETED("completed"),
    SUSPEND("suspend"),
    ONGOING("ongoing"),
    TODO("todo"),
}