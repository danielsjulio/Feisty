package com.feisty.ui.listeners;

import java.util.HashSet;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gil on 01/08/15.
 */
public class RetrofitResponseObserver<T> implements Callback<T> {

    HashSet<Callback<T>> observers = new HashSet<>();

    @Override
    public void success(T t, Response response) {
        for (Callback<T> observer : observers)
            observer.success(t, response);
    }

    @Override
    public void failure(RetrofitError error) {
        for (Callback<T> observer : observers)
            observer.failure(error);
    }

    public void addObserver(Callback<T> observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Callback<T> observer) {
        this.observers.remove(observer);
    }
}
