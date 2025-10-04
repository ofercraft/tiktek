package com.feldman.tiktek


import android.app.Application
import com.feldman.tiktek.data.network.NetworkModule
import com.feldman.tiktek.data.repo.TiktekRepository


class TiktekApp : Application() {
    lateinit var repository: TiktekRepository
        private set


    override fun onCreate() {
        super.onCreate()
        val api = NetworkModule.createApi()
        repository = TiktekRepository(api, applicationContext)
    }
}