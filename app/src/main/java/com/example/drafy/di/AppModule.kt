// AppModule.kt
package com.example.drafy.di

import android.content.Context
import androidx.room.Room
import com.example.drafy.data.local.DrafyDatabase
import com.example.drafy.data.preferences.ThemePreferences
import com.example.drafy.data.remote.WikipediaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DrafyDatabase {
        return Room.databaseBuilder(
            context,
            DrafyDatabase::class.java,
            "drafy_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: DrafyDatabase) = database.noteDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideWikipediaApi(okHttpClient: OkHttpClient): WikipediaApi {
        return Retrofit.Builder()
            .baseUrl("https://id.wikipedia.org/w/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(WikipediaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }
}