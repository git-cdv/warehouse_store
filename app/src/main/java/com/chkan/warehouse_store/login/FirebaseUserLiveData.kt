/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chkan.warehouse_store.login

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.LiveData

/**
 * Этот класс наблюдает за текущим FirebaseUser.
 * Если нет зарегистрированного пользователя, FirebaseUser будет нулевым.
 *
 * Обратите внимание, что onActive () и onInactive () будут срабатывать
 * при изменении конфигурации (например, при повороте устройства).
 * Это может быть нежелательно или дорого в зависимости от характера вашего объекта LiveData,
 * но для этой цели подходит, поскольку мы только добавляем и удаляем authStateListener
 */
class FirebaseUserLiveData : LiveData<FirebaseUser?>() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Используйте экземпляр FirebaseAuth, созданный в начале класса, чтобы получить запись
        // укажите на Firebase Authentication SDK, который использует приложение.
        // С экземпляром класса FirebaseAuth теперь вы можете запросить текущего пользователя.
        value = firebaseAuth.currentUser
    }

    // Когда у этого LiveData есть активный подписчик, начинаем наблюдать за состоянием FirebaseAuth, чтобы увидеть,
    // в настоящее время есть авторизованный пользователь.
    override fun onActive() {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    // Когда у этого объекта больше нет активного подписчика, прекращаем наблюдение за состоянием FirebaseAuth, чтобы
    // предотвращаем утечки памяти.
    override fun onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}