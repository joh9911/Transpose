package com.example.transpose.di


import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.data.repository.newpipe.NewPipeRepositoryImpl
import com.example.transpose.data.repository.suggestionkeyword.SuggestionKeywordRepository
import com.example.transpose.data.repository.suggestionkeyword.SuggestionKeywordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSuggestionKeywordRepository(
        suggestionKeywordRepositoryImpl: SuggestionKeywordRepositoryImpl
    ): SuggestionKeywordRepository

    @Binds
    @Singleton
    abstract fun bindNewPipeRepository(
        newPipeRepositoryImpl: NewPipeRepositoryImpl
    ): NewPipeRepository
}