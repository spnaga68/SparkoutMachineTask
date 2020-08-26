package com.imtamila.sparkoutmachinetask.common

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.imtamila.sparkoutmachinetask.api.ApiClient
import com.imtamila.sparkoutmachinetask.api.ServiceGenerator

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

    companion object {
        private var coreClient: ApiClient? = null

        fun getApiServiceWithBaseUrl(context: Context): ApiClient {
            if (coreClient == null) {
                coreClient = ServiceGenerator.getRetrofitClientWithUrl(context)
            }
            return coreClient as ApiClient
        }
    }
}