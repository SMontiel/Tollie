/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ollie.query;

import android.database.Cursor;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import ollie.Model;
import ollie.Ollie;
import ollie.util.QueryUtils;

public abstract class ResultQueryBase<T extends Model> extends QueryBase<T> implements ResultQuery<T> {
	public ResultQueryBase(Query parent, Class<T> table) {
		super(parent, table);
	}

	@Override
	public List<T> fetch() {
		return QueryUtils.rawQuery(mTable, getSql(), getArgs());
	}

	@Override
	public T fetchSingle() {
		List<T> results = QueryUtils.rawQuery(mTable, getSql(), getArgs());
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public <E> E fetchValue(Class<E> type) {
		final Cursor cursor = Ollie.getDatabase().rawQuery(getSql(), getArgs());
		if (!cursor.moveToFirst()) {
			return null;
		}

		if (type.equals(Byte[].class) || type.equals(byte[].class)) {
			return (E) cursor.getBlob(0);
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return (E) Double.valueOf(cursor.getDouble(0));
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return (E) Float.valueOf(cursor.getFloat(0));
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return (E) Integer.valueOf(cursor.getInt(0));
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return (E) Long.valueOf(cursor.getLong(0));
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return (E) Short.valueOf(cursor.getShort(0));
		} else if (type.equals(String.class)) {
			return (E) cursor.getString(0);
		}

		return null;
	}

	@Override
	public Observable<List<T>> observable() {
		return Observable.create(new ObservableOnSubscribe<List<T>>() {
			@Override
			public void subscribe(ObservableEmitter<List<T>> emitter) {
				final List<T> result = fetch();
				if (!emitter.isDisposed()) {
					emitter.onNext(result);
					emitter.onComplete();
				}
			}
		});
	}

	@Override
	public Observable<T> observableSingle() {
		return Observable.create(new ObservableOnSubscribe<T>() {
			@Override
			public void subscribe(ObservableEmitter<T> emitter) throws Exception {
				final T result = fetchSingle();
				if (!emitter.isDisposed()) {
					emitter.onNext(result);
					emitter.onComplete();
				}
			}
		});
	}

	@Override
	public <E> Observable<E> observableValue(final Class<E> type) {
		return Observable.create(new ObservableOnSubscribe<E>() {
			@Override
			public void subscribe(ObservableEmitter<E> emitter) throws Exception {
				final E result = fetchValue(type);
				if (!emitter.isDisposed()) {
					emitter.onNext(result);
					emitter.onComplete();
				}
			}
		});
	}
}
