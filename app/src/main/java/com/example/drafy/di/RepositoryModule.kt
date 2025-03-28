// RepositoryModule.kt
package com.example.drafy.di

import com.example.drafy.data.local.dao.NoteDao
import com.example.drafy.data.remote.WikipediaApi
import com.example.drafy.data.remote.WikipediaRepository
import com.example.drafy.data.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }

    @Provides
    @Singleton
    fun provideWikipediaRepository(wikipediaApi: WikipediaApi): WikipediaRepository {
        return WikipediaRepository(wikipediaApi)
    }
}