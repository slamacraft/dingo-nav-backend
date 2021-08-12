package com.dingdo.module.game.cdda.data.component

import gnu.gettext.GettextResource
import java.util.*


object GettextTranslator {

    private var bundle = ResourceBundle.getBundle("cataclysm-dda", Locale("zh", "CN"))

    fun translation(message: String): String {
        return GettextResource.gettext(bundle, message)
    }
}

fun main() {
    println(GettextTranslator.translation("bouncer zombie"))
}
