package studios.devs.mobi.model.result

/**
 * Used from the service layer to return
 */
sealed class Result<Data> {
    /**
     * Represents a Loading state of the request
     */
    data class Loading<Data>(var unit: Unit = Unit) : Result<Data>()

    /**
     * Represents a success from the request
     * @param data The data received successfully
     */
    data class Success<Data>(var data: Data) : Result<Data>()

    /**
     * Represents an Error
     * @param error the error to be passed to the view
     * @see IResultError
     */
    data class Error<Data>(var error: IResultError) : Result<Data>()

}
