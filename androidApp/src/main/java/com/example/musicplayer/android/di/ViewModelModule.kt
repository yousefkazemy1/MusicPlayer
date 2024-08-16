import com.example.musicplayer.android.ui.main.MainViewModel
import com.example.musicplayer.android.ui.tracks.MusicTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel()
    }

    viewModel {
        MusicTracksViewModel(get(), get(), get())
    }
}