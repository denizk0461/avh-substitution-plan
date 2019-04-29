package com.denizd.substitutionplan;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SubstViewModel extends AndroidViewModel {

    private SubstRepository repository;
    private LiveData<List<Subst>> allSubst;

    public SubstViewModel(@NonNull Application application) {
        super(application);
        repository = new SubstRepository(application);
        allSubst = repository.getAllSubst();
    }

    public void insert(Subst subst) {
        repository.insert(subst);
    }

    public void update(Subst subst) {
        repository.update(subst);
    }

    public void delete(Subst subst) {
        repository.delete(subst);
    }

    public void deleteAllSubst() {
        repository.deleteAllSubst();
    }

    public LiveData<List<Subst>> getAllSubst() {
        return allSubst;
    }
}
