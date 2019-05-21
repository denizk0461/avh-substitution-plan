package com.denizd.substitutionplan

class DataGetter {

    var boy = arrayOf("Cedric Grislawski", "Dawid Kniola", "Jonas Köstergarten", "Jakob Moerschner", "Siamak Nemati", "Hendrik Plönnings", "Jan Rohmann", "Anton Rosenberger", "Christoph Senft", "Jan Mika Sieckendieck", "Alexander Sinaj", "Nico Tischer", "Nhat Vinh Tran", "Leon Becker", "Youssef Beltagi", "Erich Kerkesner", "Alex Lick", "Oskar Piskorz", "Theo Recktor", "Eray Vatansever", "Dennis Wojciechowski", "Übeydullah Alkan", "Yahya Almarashli", "Sinan Aydin", "Rick Büteröwe", "Levin Dropp", "Silvester Jermolajew", "Tolgay Kekilli", "Dennis Lange", "Aras Mahmoud", "Batuhan Özcan", "Michael Rek", "Khabat Sharif", "Anbinh Tran", "Bastian Westerboer", "Abdulwahab Alshebli", "Kelechi Gaius", "Armin Großmann", "Anes Halep", "Marco Jankowski", "Emirhan Kaplan", "Alexander Kofmann", "Tobi Derek Köhler", "Vasilije Nedeljkovic", "Batu Öcal", "Paul Rehbohm")
    var girl = arrayOf("Mayra Dronia", "Shalin Ramadan", "Nigina Amin", "Mariya Kheder", "Niegen Khosrawi", "Dayana Osipova", "Julia Petri", "Laura Pister", "Dana Alyoussef", "Ilkem Kahramanoglu", "Rozita Amiri", "Mirja Arneke", "Nastasja Banik", "Anni Kienke", "Benedite Mateta", "Leonie Pohl", "Cecillia Sawires", "Patrycja Smółka", "Elif Zencirkiran", "Nusyba Al Semadi", "Vivien Bruns", "Yasemin Dal", "Svenja Demuth", "Nina Groß", "Kim Lahmeyer", "Luca Minschke", "Kim Reschke", "Amélie Schnellecke", "Delal Secer", "Xenia Segijanski", "Melisa Tas", "Kim Völker", "Banu Yari", "Zeinab Ali", "Mariam Bannout", "Stefanie Fahl", "Yamaty Gaye", "Anna-Sophia Haeberle", "Finja Hastedt", "Viktoria Hammermeister", "Isabel Horn", "Laura Kern", "Melanie Kleinermann", "Målin Kollhoff", "Darja Maier", "Lisa Maier", "Naura Nadzifah", "Janina Roelfs", "Vanessa Scheifler", "Mara Thies", "Lieli Umar", "Leonie Welk", "Betraben Yacoub")

    fun getIcon(course: String): Int {
        return if (course.toLowerCase().contains("deu") || course.toLowerCase().contains("dep") || course.toLowerCase().contains("daz")) {
            R.drawable.ic_german
        } else if (course.toLowerCase().contains("mat") || course.toLowerCase().contains("map")) {
            R.drawable.ic_maths
        } else if (course.toLowerCase().contains("eng") || course.toLowerCase().contains("enp") || course.toLowerCase().contains("ena")) {
            R.drawable.ic_english
        } else if (course.toLowerCase().contains("spo") || course.toLowerCase().contains("spp") || course.toLowerCase().contains("spth")) {
            R.drawable.ic_pe
        } else if (course.toLowerCase().contains("pol") || course.toLowerCase().contains("pop")) {
            R.drawable.ic_politics
        } else if (course.toLowerCase().contains("dar") || course.toLowerCase().contains("dap")) {
            R.drawable.ic_drama
        } else if (course.toLowerCase().contains("phy") || course.toLowerCase().contains("php")) {
            R.drawable.ic_physics
        } else if (course.toLowerCase().contains("bio") || course.toLowerCase().contains("bip") || course.toLowerCase().contains("nw")) {
            R.drawable.ic_biology
        } else if (course.toLowerCase().contains("che") || course.toLowerCase().contains("chp")) {
            R.drawable.ic_chemistry
        } else if (course.toLowerCase().contains("phi") || course.toLowerCase().contains("psp")) {
            R.drawable.ic_philosophy
        } else if (course.toLowerCase().contains("laa") || course.toLowerCase().contains("laf") || course.toLowerCase().contains("lat")) {
            R.drawable.ic_latin
        } else if (course.toLowerCase().contains("spa") || course.toLowerCase().contains("spf")) {
            R.drawable.ic_spanish
        } else if (course.toLowerCase().contains("fra") || course.toLowerCase().contains("frf") || course.toLowerCase().contains("frz")) {
            R.drawable.ic_french
        } else if (course.toLowerCase().contains("inf")) {
            R.drawable.ic_compsci
        } else if (course.toLowerCase().contains("ges")) {
            R.drawable.ic_history
        } else if (course.toLowerCase().contains("rel")) {
            R.drawable.ic_religion
        } else if (course.toLowerCase().contains("geg") || course.toLowerCase().contains("wuk")) {
            R.drawable.ic_geography
        } else if (course.toLowerCase().contains("kun")) {
            R.drawable.ic_arts
        } else if (course.toLowerCase().contains("mus")) {
            R.drawable.ic_music
        } else if (course.toLowerCase().contains("tue")) {
            R.drawable.ic_turkish
        } else if (course.toLowerCase().contains("chi")) {
            R.drawable.ic_chinese
        } else if (course.toLowerCase().contains("gll")) {
            R.drawable.ic_gll
        } else if (course.toLowerCase().contains("wat")) {
            R.drawable.ic_wat
        } else if (course.toLowerCase().contains("för")) {
            R.drawable.ic_help
        } else if (course.toLowerCase().contains("wp") || course.toLowerCase().contains("met")) {
            R.drawable.ic_pencil
        } else {
            R.drawable.ic_empty
        }
    }
}
