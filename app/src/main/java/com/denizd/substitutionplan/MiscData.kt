package com.denizd.substitutionplan

object MiscData {

    fun emoji(unicode: Int): String { return String(Character.toChars(unicode)) }

    fun getIcon(course: String): Int {
        return with (course.toLowerCase()) {
            when {
                contains("deu") || contains("dep") || contains("daz") -> R.drawable.ic_german
                contains("mat") || contains("map") -> R.drawable.ic_maths
                contains("eng") || contains("enp") || contains("ena") -> R.drawable.ic_english
                contains("spo") || contains("spp") || contains("spth") -> R.drawable.ic_pe
                contains("pol") || contains("pop") -> R.drawable.ic_politics
                contains("dar") || contains("dap") -> R.drawable.ic_drama
                contains("phy") || contains("php") -> R.drawable.ic_physics
                contains("bio") || contains("bip") || contains("nw") -> R.drawable.ic_biology
                contains("che") || contains("chp") -> R.drawable.ic_chemistry
                contains("phi") || contains("psp") -> R.drawable.ic_philosophy
                contains("laa") || contains("laf") || contains("lat") -> R.drawable.ic_latin
                contains("spa") || contains("spf") -> R.drawable.ic_spanish
                contains("fra") || contains("frf") || contains("frz") -> R.drawable.ic_french
                contains("inf") -> R.drawable.ic_compsci
                contains("ges") -> R.drawable.ic_history
                contains("rel") -> R.drawable.ic_religion
                contains("geg") || contains("wuk") -> R.drawable.ic_geography
                contains("kun") -> R.drawable.ic_arts
                contains("mus") -> R.drawable.ic_music
                contains("tue") -> R.drawable.ic_turkish
                contains("chi") -> R.drawable.ic_chinese
                contains("gll") -> R.drawable.ic_gll
                contains("wat") -> R.drawable.ic_wat
                contains("för") -> R.drawable.ic_help
                contains("wp") || contains("met") -> R.drawable.ic_pencil
                else -> R.drawable.ic_empty
            }
        }
    }
}
