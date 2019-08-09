package com.denizd.substitutionplan

import android.content.SharedPreferences

object MiscData {

    fun emoji(unicode: Int): String { return String(Character.toChars(unicode)) }

    fun getColourForString(name: String): Int {
        return when(name) {
            "red" -> R.color.bgRed
            "orange" -> R.color.bgOrange
            "yellow" -> R.color.bgYellow
            "green" -> R.color.bgGreen
            "teal" -> R.color.bgTeal
            "cyan" -> R.color.bgCyan
            "blue" -> R.color.bgBlue
            "purple" -> R.color.bgPurple
            "pink" -> R.color.bgPink
            "brown" -> R.color.bgBrown
            "grey" -> R.color.bgGrey
            "pureWhite" -> R.color.bgPureWhite
            "salmon" -> R.color.bgSalmon
            "tangerine" -> R.color.bgTangerine
            "banana" -> R.color.bgBanana
            "flora" -> R.color.bgFlora
            "spindrift" -> R.color.bgSpindrift
            "sky" -> R.color.bgSky
            "orchid" -> R.color.bgOrchid
            "lavender" -> R.color.bgLavender
            "carnation" -> R.color.bgCarnation
            "brown2" -> R.color.bgBrown2
            "pureBlack" -> R.color.bgPureBlack
            else -> R.color.colorBackgroundLight
        }
    }

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

    fun transferOldColourIntsToString(prefs: SharedPreferences) {
        val colourIntegers = intArrayOf(0, R.color.bgRed, R.color.bgOrange, R.color.bgYellow, R.color.bgGreen,
                R.color.bgTeal, R.color.bgCyan, R.color.bgBlue, R.color.bgPurple, R.color.bgPink, R.color.bgBrown, R.color.bgGrey,
                R.color.bgPureWhite, R.color.bgSalmon, R.color.bgTangerine, R.color.bgBanana, R.color.bgFlora, R.color.bgSpindrift,
                R.color.bgSky, R.color.bgOrchid, R.color.bgLavender, R.color.bgCarnation, R.color.bgBrown2, R.color.bgPureBlack)
        val colourStrings = arrayOf("default", "red", "orange", "yellow", "green", "teal", "cyan", "blue", "purple", "pink",
                "brown", "grey", "pureWhite", "salmon", "tangerine", "banana", "flora", "spindrift", "sky", "orchid",
                "lavender", "carnation", "brown2", "pureBlack")
        val courses = arrayOf("German", "English", "French", "Spanish", "Latin", "Turkish", "Chinese", "Arts", "Music",
                "Theatre", "Geography", "History", "Politics", "Philosophy", "Religion", "Maths", "Biology", "Chemistry",
                "Physics", "CompSci", "PhysEd", "GLL", "WAT", "Forder", "WP")
        var colour = ""
        val edit = prefs.edit()
        for (course in courses) {
            for (i in 0 until colourIntegers.size) {
                if (prefs.getInt("bg$course", 0) == colourIntegers[i]) {
                    colour = colourStrings[i]
                    break
                }
                colour = ""
            }
            edit.putString("card$course", colour)
        }
        edit.apply()
    }
}
