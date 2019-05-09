package com.denizd.substitutionplan;

public class DataGetter {

    protected static int getIcon(String course) {
        if (course.toLowerCase().contains("deu") || course.toLowerCase().contains("dep") || course.toLowerCase().contains("daz")) {
            return R.drawable.ic_german;
        } else if (course.toLowerCase().contains("mat") || course.toLowerCase().contains("map")) {
            return R.drawable.ic_maths;
        } else if (course.toLowerCase().contains("eng") || course.toLowerCase().contains("enp") || course.toLowerCase().contains("ena")) {
            return R.drawable.ic_english;
        } else if (course.toLowerCase().contains("spo") || course.toLowerCase().contains("spp") || course.toLowerCase().contains("spth")) {
            return R.drawable.ic_pe;
        } else if (course.toLowerCase().contains("pol") || course.toLowerCase().contains("pop")) {
            return R.drawable.ic_politics;
        } else if (course.toLowerCase().contains("dar") || course.toLowerCase().contains("dap")) {
            return R.drawable.ic_drama;
        } else if (course.toLowerCase().contains("phy") || course.toLowerCase().contains("php")) {
            return R.drawable.ic_physics;
        } else if (course.toLowerCase().contains("bio") || course.toLowerCase().contains("bip") || course.toLowerCase().contains("nw")) {
            return R.drawable.ic_biology;
        } else if (course.toLowerCase().contains("che") || course.toLowerCase().contains("chp")) {
            return R.drawable.ic_chemistry;
        } else if (course.toLowerCase().contains("phi") || course.toLowerCase().contains("psp")) {
            return R.drawable.ic_philosophy;
        } else if (course.toLowerCase().contains("laa") || course.toLowerCase().contains("laf") || course.toLowerCase().contains("lat")) {
            return R.drawable.ic_latin;
        } else if (course.toLowerCase().contains("spa") || course.toLowerCase().contains("spf")) {
            return R.drawable.ic_spanish;
        } else if (course.toLowerCase().contains("fra") || course.toLowerCase().contains("frf") || course.toLowerCase().contains("frz")) {
            return R.drawable.ic_french;
        } else if (course.toLowerCase().contains("inf")) {
            return R.drawable.ic_compsci;
        } else if (course.toLowerCase().contains("ges")) {
            return R.drawable.ic_history;
        } else if (course.toLowerCase().contains("rel")) {
            return R.drawable.ic_religion;
        } else if (course.toLowerCase().contains("geg") || course.toLowerCase().contains("wuk")) {
            return R.drawable.ic_geography;
        } else if (course.toLowerCase().contains("kun")) {
            return R.drawable.ic_arts;
        } else if (course.toLowerCase().contains("mus")) {
            return R.drawable.ic_music;
        } else if (course.toLowerCase().contains("tue")) {
            return R.drawable.ic_turkish;
        } else if (course.toLowerCase().contains("chi")) {
            return R.drawable.ic_chinese;
        } else if (course.toLowerCase().contains("gll")) {
            return R.drawable.ic_gll;
        } else if (course.toLowerCase().contains("wat")) {
            return R.drawable.ic_wat;
        } else if (course.toLowerCase().contains("för")) {
            return R.drawable.ic_help;
        } else if (course.toLowerCase().contains("wp") || course.toLowerCase().contains("met")) {
            return R.drawable.ic_pencil;
        } else {
            return R.drawable.ic_empty;
        }
    }

    protected static String[] boy = {"Cedric Grislawski", "Dawid Kniola", "Jonas Köstergarten", "Jakob Moerschner", "Siamak Nemati", "Hendrik Plönnings",
            "Jan Rohmann", "Anton Rosenberger", "Christoph Senft", "Jan Mika Sieckendieck", "Alexander Sinaj",
            "Nico Tischer", "Nhat Vinh Tran", "Leon Becker", "Youssef Beltagi", "Erich Kerkesner", "Alex Lick",
            "Oskar Piskorz", "Theo Recktor", "Eray Vatansever", "Dennis Wojciechowski", "Übeydullah Alkan",
            "Yahya Almarashli", "Sinan Aydin", "Rick Büteröwe", "Levin Dropp", "Silvester Jermolajew", "Tolgay Kekilli",
            "Dennis Lange", "Aras Mahmoud", "Batuhan Özcan", "Michael Rek", "Khabat Sharif", "Anbinh Tran",
            "Bastian Westerboer", "Abdulwahab Alshebli", "Kelechi Gaius", "Armin Großmann", "Anes Halep",
            "Marco Jankowski", "Emirhan Kaplan", "Alexander Kofmann", "Tobi Derek Köhler", "Vasilije Nedeljkovic",
            "Batu Öcal", "Paul Rehbohm"};
    protected static String[] girl = {"Mayra Dronia", "Shalin Ramadan", "Nigina Amin", "Mariya Kheder", "Niegen Khosrawi", "Dayana Osipova",
            "Julia Petri", "Laura Pister", "Dana Alyoussef", "Ilkem Kahramanoglu", "Rozita Amiri", "Mirja Arneke",
            "Nastasja Banik", "Anni Kienke", "Benedite Mateta", "Leonie Pohl", "Cecillia Sawires", "Patrycja Smółka",
            "Elif Zencirkiran", "Nusyba Al Semadi", "Vivien Bruns", "Yasemin Dal", "Svenja Demuth", "Nina Groß",
            "Kim Lahmeyer", "Luca Minschke", "Kim Reschke", "Amélie Schnellecke", "Delal Secer", "Xenia Segijanski",
            "Melisa Tas", "Kim Völker", "Banu Yari", "Zeinab Ali", "Mariam Bannout", "Stefanie Fahl", "Yamaty Gaye",
            "Anna-Sophia Haeberle", "Finja Hastedt", "Viktoria Hammermeister", "Isabel Horn", "Laura Kern",
            "Melanie Kleinermann", "Målin Kollhoff", "Darja Maier", "Lisa Maier", "Naura Nadzifah", "Janina Roelfs",
            "Vanessa Scheifler", "Mara Thies", "Lieli Umar", "Leonie Welk", "Betraben Yacoub"};
}
