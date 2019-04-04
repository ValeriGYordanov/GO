package studios.devs.mobi.model

sealed class Screen<T>(var someData: T?){

    data class OfflineSpot<T>(var data: T?) : Screen<T>(data)

}