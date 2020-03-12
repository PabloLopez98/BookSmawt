package pablo.myexample.booksmawt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Communicator : ViewModel() {

    var profileObj = MutableLiveData<Profile>()

    var bookObj = MutableLiveData<Book>()

    var filterObj = MutableLiveData<Filter>()

    var buyerObj = MutableLiveData<ChatProfile>()

    fun passProfileObj(obj: Profile) {
        profileObj.value = obj
    }

    fun passBookObj(obj: Book) {
        bookObj.value = obj
    }

    fun passFilterObj(obj: Filter){
        filterObj.value = obj
    }

    fun passBuyerObj(obj: ChatProfile){
        buyerObj.value = obj
    }
}