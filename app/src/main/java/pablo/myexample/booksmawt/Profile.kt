package pablo.myexample.booksmawt

data class Profile(var name: String, var location: String, var url: String){
  constructor() : this("", "", "")
}