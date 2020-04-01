# BookSmawt
## Purpose Of The App
BookSmawt is an app on the google playstore which allows one to buy or sell used books by searching or posting in a specific city. Books are identified by their 13 digit ISBN number.
## Activities
There are 4 activities:
- Before Signing In
1) MainActivity (Splash screen activity)
2) Login (Activity where users sign in with valid email/password, google, or twitter)
3) CreateAccount (register via email and password activity)
- After Signing In
4) Base (activity which holds every fragment)
## Fragments Held By 'Base' Activity
1) AddedBookFragment (User views the book he/she uploaded)
2) BookDetails (User views a seller's posted book)
3) ChatFragment (chat room where user sends messages to buyer/seller)
4) EditAddedBookFragment (User edits their uploaded book)
5) EditProfile (User edits their displayed profile)
6) ListFragment (User sees a list of all the books he/she uploaded)
7) MessagesFragment (User sees a list of all the people he/she is chatting with)
8) PreviewFragment (User sees a preview of the uploaded book once inside the ChatFragment)
9) ProfileFragment (User sees his/her profile info)
10) SearchFragment (User searches for a book via 13 digit ISBN number)
11) SearchFilterFragment (filter search by city and price)
12) AddFragment (User uploads a book to sell)
## Data Classes
1) Book
2) ChatProfile
3) Filter
4) LastMessage
5) Message
6) Profile
## RecyclerView Adapters
1) ListFragmentAdapter (to display books uploaded)
2) MessagesFragmentAdapter (to display users current user is chatting with)
## ViewModel
1) Communicator (holds different methods to transfer live data)
## Service
1) Service (pushes local notification to user if a text message arrives)
## Other
1) SendPush (class holds method to send a push notification to user in opposite end of chat)
## Screenshots
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112358-f92bed80-73b2-11ea-9ee1-2cd254cd793d.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/72857457-809e0780-3c72-11ea-82f4-867028f15935.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/72857577-e7bbbc00-3c72-11ea-9a23-ebe6c77b5959.png" width="200">
<img src="https://user-images.githubusercontent.com/51018556/72857583-ebe7d980-3c72-11ea-91a3-da671691a653.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/72857598-f3a77e00-3c72-11ea-9ee3-38cf3c6f77fc.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/72857574-e5596200-3c72-11ea-9e8b-ef369164f296.png" width="200">



