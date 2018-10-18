package pl.helenium.mockingbird.model

typealias ModelError = String

interface Validator {

    fun validate(context: Context, metaModel: MetaModel, actor: Actor?, model: Model, errors: MutableList<ModelError>)

}
