package cl.abastible.ftgr.app.di

import cl.abastible.ftgr.app.data.remote.GoogleMapsApiService
import cl.abastible.ftgr.app.data.remote.ServicesApi
import cl.abastible.ftgr.app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideServicesApi(retrofit: Retrofit): ServicesApi {
        return retrofit.create(ServicesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleMapsApiService(retrofit: Retrofit): GoogleMapsApiService {
        return retrofit.create(GoogleMapsApiService::class.java)
    }
}
