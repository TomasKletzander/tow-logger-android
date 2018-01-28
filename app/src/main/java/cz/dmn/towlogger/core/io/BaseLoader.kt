package cz.dmn.towlogger.core.io

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseLoader<T> {

    private val disposables = CompositeDisposable()

    fun <O : Disposable> execute(disposableObserver: O) where O : Observer<T> {
        this.disposables.add(buildObservable()
                .compose(applySchedulers())
                .subscribeWith(disposableObserver))
    }

    protected abstract fun buildObservable(): Observable<T>

    private fun <X> applySchedulers(): ObservableTransformer<X, X> {
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}
