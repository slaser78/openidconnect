package openidconnect

class ContentController {

    def index() {
        def map = [book: Book.get(params.id)]
        render(view: "index", model: map)
    }
}
