package studios.devs.mobi.extension

import android.view.View
import android.widget.Adapter
import android.widget.EditText
import android.widget.GridView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

var View.rxClick: Observable<Unit>
    get() = RxView.clicks(this).map { Unit }
    set(value) {}


var EditText.rxTextChanges: Observable<String>
    get() = RxTextView.textChanges(this).map { it.toString() }
    set(value) {}


var GridView.rxItemClick: Observable<Pair<Adapter, Int>>
    get() = RxAdapterView.itemClicks(this).map { Pair(this.adapter, it) }
    set(value) {}
