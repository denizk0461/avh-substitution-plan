package com.denizd.substitutionplan;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Subst.class}, version = 2, exportSchema = false)
public abstract class SubstDatabase extends RoomDatabase {

    private static SubstDatabase instance;

    public abstract SubstDao substDao();

    public static synchronized SubstDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SubstDatabase.class,
                    "subst_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

//    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() { // initial population
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//            new PopulateDBAsync(instance).execute();
//        }
//    };
//
//    private static class PopulateDBAsync extends AsyncTask<Void, Void, Void> {
//
//        private SubstDao substDao;
//
//        private PopulateDBAsync(SubstDatabase db) {
//            substDao = db.substDao();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            substDao.insert(new Subst(R.drawable.ic_avh,"Group", "Date", "Time", "Course", "Room", "Additional", 0));
//            return null;
//        }
//    }
}
