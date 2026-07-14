package za.co.ndivhuwo.stackoverflow_search_app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import za.co.ndivhuwo.stackoverflow_search_app.data.repository.StackOverflowRepository
import za.co.ndivhuwo.stackoverflow_search_app.data.repository.StackOverflowRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStackOverflowRepository(
        impl: StackOverflowRepositoryImpl
    ): StackOverflowRepository
}
