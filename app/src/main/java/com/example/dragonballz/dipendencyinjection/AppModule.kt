package com.example.dragonballz.dipendencyinjection

import android.app.Application
import androidx.room.Room
import com.example.dragonballz.data.local.database.DragonBallzDatabase
import com.example.dragonballz.data.remote.api.DragonBallzApi
import com.example.dragonballz.repository.CharacterRepository
import com.example.dragonballz.repository.CharacterRepositoryImpl
import com.example.dragonballz.util.Constants.BASE_URL
import com.example.dragonballz.viewmodel.CharacterDetailsViewModel
import com.example.dragonballz.viewmodel.CharacterListViewModel
import com.example.dragonballz.viewmodel.CharacterCompareViewModel
import com.example.dragonballz.viewmodel.FavoriteCharactersViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DragonBallzApi::class.java)
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get<Application>(),
            DragonBallzDatabase::class.java,
            "dragonballz_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single {
        get<DragonBallzDatabase>().dragonBallzDao()
    }
}

val repositoryModule = module {
    single<CharacterRepository> {
        CharacterRepositoryImpl(get(), get())
    }
}

val viewModelModule = module {
    viewModel { CharacterListViewModel(get(), get()) }
    viewModel { CharacterDetailsViewModel(get(), get(), get()) }
    viewModel { FavoriteCharactersViewModel(get()) }
    viewModel { CharacterCompareViewModel(get()) }
}