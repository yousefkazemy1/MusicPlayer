shared {
    Sql delight -> store {
        play lists,
        favorites
    },
    ktor -> cloud {
        upload tracks,
        download tracks,
        sign in and sign up to cloud account
    },
    coin -> dependency injection,
    compose -> ui {
        MainActivity -> screens {
            favorites,
            play lists,
            tracks,
            albums,
            artists,
            folders,
            search,
            sub directory tracks
        },
        MainActivity -> components {
            floating player View
            toolbar,
            edit action View
        }
    },
    viewModel
}

platform specific {
    MainActivity,
    music content provider,
    Sound player component,
    Sound playing background service functionalities,
    notification
}