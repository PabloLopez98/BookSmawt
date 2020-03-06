package pablo.myexample.booksmawt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Communicator : ViewModel() {

    var profileObj = MutableLiveData<Profile>()

    var bookObj = MutableLiveData<Book>()

    fun passProfileObj(obj: Profile) {
        profileObj.value = obj
    }

    fun passBookObj(obj: Book) {
        bookObj.value = obj
    }
}