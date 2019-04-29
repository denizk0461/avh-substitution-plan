package com.denizd.substitutionplan;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class SubstRepository {

    private SubstDao substDao;
    private LiveData<List<Subst>> allSubst;

    public SubstRepository(Application application) {
        SubstDatabase database = SubstDatabase.getInstance(application);
        substDao = database.substDao();
        allSubst = substDao.getAllSubst();
    }

    public void insert(Subst subst) {
        new InsertSubstAsync(substDao).execute(subst);
    }

    public void update(Subst subst) {
        new UpdateSubstAsync(substDao).execute(subst);
    }

    public void delete(Subst subst) {
        new DeleteSubstAsync(substDao).execute(subst);
    }

    public void deleteAllSubst() {
        new DeleteAllSubstAsync(substDao).execute();
    }

    public LiveData<List<Subst>> getAllSubst() {
        return allSubst;
    }

    private static class InsertSubstAsync extends AsyncTask<Subst, Void, Void> {

        private SubstDao substDao;
        private InsertSubstAsync(SubstDao substDao) {
            this.substDao = substDao;
        }

        @Override
        protected Void doInBackground(Subst... substs) {
            substDao.insert(substs[0]);
            return null;
        }
    }

    private static class UpdateSubstAsync extends AsyncTask<Subst, Void, Void> {

        private SubstDao substDao;
        private UpdateSubstAsync(SubstDao substDao) {
            this.substDao = substDao;
        }

        @Override
        protected Void doInBackground(Subst... substs) {
            substDao.update(substs[0]);
            return null;
        }
    }

    private static class DeleteSubstAsync extends AsyncTask<Subst, Void, Void> {

        private SubstDao substDao;
        private DeleteSubstAsync(SubstDao substDao) {
            this.substDao = substDao;
        }

        @Override
        protected Void doInBackground(Subst... substs) {
            substDao.delete(substs[0]);
            return null;
        }
    }

    private static class DeleteAllSubstAsync extends AsyncTask<Void, Void, Void> {

        private SubstDao substDao;
        private DeleteAllSubstAsync(SubstDao substDao) {
            this.substDao = substDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            substDao.deleteAllSubst();
            return null;
        }
    }
}
