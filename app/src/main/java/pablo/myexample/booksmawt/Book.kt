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
) : Comparable<Book> {
    constructor() : this(ArrayList(), "", "", "", "", "", "", "", "", "")

    override fun compareTo(other: Book): Int {
        var cT : Int = other.price.toInt()
        return price.toInt() - cT
    }
}