package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.definition.Authenticator
import pl.helenium.mockingbird.exception.BadRequest
import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Authorization
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.Direction
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.model.Order
import pl.helenium.mockingbird.model.OrderBy
import pl.helenium.mockingbird.model.Page
import pl.helenium.mockingbird.model.PageRequest
import pl.helenium.mockingbird.server.Request

class BearerAuthenticator(
    private val context: Context,
    private val scope: String
) : Authenticator {

    override fun invoke(request: Request): Actor? = context
        .actors
        .scope(scope)
        .authorize(request.authorization())

    private fun Request.authorization() = header("Authorization")
        ?.takeIf { it.startsWith("Bearer ") }
        ?.removePrefix("Bearer ")
        ?.let(::Authorization)

}

fun dataMetaUnwrapper(model: Model) = model.embeddedModel("data")

fun dataMetaWrapper(metaModel: MetaModel): (Model) -> Model = {
    Model(
        mapOf(
            "data" to it.asMap(),
            "meta" to mapOf(
                "type" to metaModel.name
            )
        )
    )
}

fun itemsWrapper(page: Page<Model>) = Model(
    mapOf(
        "items" to page
            .items
            .map(Model::asMap),
        "meta" to mapOf(
            "type" to "collection",
            "count" to page.items.size
        )
    )
)

fun pageRequestExtractor(request: Request) = PageRequest(
    request
        .queryParam("page")
        ?.toInt()
        ?.dec()
        ?: 0,
    request
        .queryParam("per_page")
        ?.toInt()
        ?: 25
        // FIXME max 100
)

fun orderByExtractor(metaModel: MetaModel, request: Request): OrderBy? {
    val sortBy = request.queryParam("sort_by") ?: return null
    val matchResult = """(?<property>\w+)(:(?<direction>asc|desc))?"""
        .toRegex()
        .matchEntire(sortBy)
        ?: throw BadRequest()
    return OrderBy(
        Order(
            metaModel.property(matchResult.groups["property"]!!.value),
            (matchResult.groups["direction"]?.value ?: "asc")
                .let(String::toUpperCase)
                .let(Direction::valueOf)
        )
    )
}
