package com.jankku.eino.di

import android.content.Context
import com.jankku.eino.BuildConfig
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.TokenInterceptor
import com.jankku.eino.network.TokenRenewInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideEinoApi(retrofit: Retrofit): EinoApi =
        retrofit.create(EinoApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(dataStoreManager: DataStoreManager, moshi: Moshi): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addNetworkInterceptor(TokenInterceptor(dataStoreManager))
            .addInterceptor(TokenRenewInterceptor(dataStoreManager, moshi))
            .build()

    @Provides
    @Singleton
    fun provideAuthenticator(
        dataStoreManager: DataStoreManager,
        moshi: Moshi
    ): TokenRenewInterceptor = TokenRenewInterceptor(dataStoreManager, moshi)

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)
}