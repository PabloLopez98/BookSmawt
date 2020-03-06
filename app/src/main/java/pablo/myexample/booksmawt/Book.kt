package pablo.myexample.booksmawt

data class Book(
    var urlList: ArrayList<String>,
    var title: String,
    var author: String,
    var isbn: String,
    var price: String,
    var details: String,
    var location: String,
    var urlOfOwner: String,
    var nameOfOwner: String,
    var idOfOwner: String
) {
    constructor() : this(ArrayList(), "", "", "", "", "", "", "", "", "")
}