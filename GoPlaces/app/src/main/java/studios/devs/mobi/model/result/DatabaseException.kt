package studios.devs.mobi.model.result

abstract class DatabaseException(override var description: String): ResultError(description){
    class DuplicateName(description: String = "You have already inserted spot with this name.") : DatabaseException(description)
    class LoadingFailed(description: String = "Loading from database failed.") : DatabaseException(description)
}