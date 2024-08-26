package com.example.transpose.navigation

sealed class Route(val route: String){
    data object Home: Route("home"){
        data object Playlist: Route("home/playlist")
        data object PlaylistItem: Route("home/playlist_item/{itemId}"){
            fun createRoute(itemId: String?) = "home/playlist_item/$itemId"
        }
        data object SearchResult: Route("home/search_result/{query}"){
            fun createRoute(query: String?) = "home/search_result/$query"
        }
    }
    data object Convert: Route("convert"){
        data object AudioEdit: Route("convert/audio_edit")
        data object SearchResult: Route("convert/search_result/{query}"){
            fun createRoute(query: String?) = "convert/search_result/$query"
        }
    }
    data object Library: Route("library"){
        data object MyPlaylist: Route("library/my_playlist")
        data object MyPlaylistItem: Route("library/my_playlist_item/{itemId}"){
            fun createRoute(itemId: String?) = "library/my_playlist_item/$itemId"
        }
        data object SearchResult: Route("library/search_result/{query}"){
            fun createRoute(query: String?) = "library/search_result/$query"
        }
        data object MyLocalFileItem: Route("library/my_local_file_item/{type}"){
            fun createRoute(type: String?) = "library/my_local_file_item/$type"

        }
    }
}