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
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112362-fa5d1a80-73b2-11ea-8265-5bac7e593bca.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112365-faf5b100-73b2-11ea-8084-d8dd4b6f769a.png" width="200">
<img src="https://user-images.githubusercontent.com/51018556/78112367-fb8e4780-73b2-11ea-9dc8-56fc7c27eba0.png"
width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112371-fcbf7480-73b2-11ea-9a65-042798294c68.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112378-fdf0a180-73b2-11ea-9183-06b1f5bc0131.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112381-fe893800-73b2-11ea-9fac-e34a8b520ec9.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112383-ffba6500-73b2-11ea-9999-e2fb1f961fcd.png" width="200">
<img align="left" src="https://user-images.githubusercontent.com/51018556/78112385-0052fb80-73b3-11ea-92a8-74ddb373166d.png" width="200">



