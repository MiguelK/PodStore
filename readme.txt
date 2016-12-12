Todo:
Subscription
    - push
    - save/load data
    - Episode notifier job -> push episode
Start
    - save/load date
    -

Admin
http://localhost:10080/PodStore

----   API ------
[podCastCatalog]
http://localhost:10080/PodStore/api/podCastCatalog?lang='SV'
http://localhost:10080/PodStore/api/podCastCatalog/search?lang=SE&query=Java
http://localhost:10080/PodStore/api/podCastCatalog/search?lang=SE&query=term=p3&entity=podcast&limit=3

[podCast]
http://localhost:10080/PodStore/api/podCast?id='podCastCollectionId'

[subscriber]
GET http://localhost:10080/PodStore/api/subscriber   (Get status)

[podCastSubscription]
GET http://localhost:10080/PodStore/api/podCastSubscription   (Get status)

** ZIP file ****
http://localhost:10080/PodStore/jsonfile?lang=SE
Itunes Search API
https://itunes.apple.com/lookup?id=308339623

---
# Episode has podCastRefId = unikt id inom samma catalog som pekar på PodCast
# Search
		-Client can have a PodCast not existsing in server catalog.

* Subscriber.getSubscriberPodCasts()
   get request from playListVC, update with star if later episode exist
   use the return podcast values if later exists


# Startup of server
		 - Index all Episodes in in-memory searchable cache
		0# /api/search/query=p3&lang=ALL
		1# search for PodCasts on Itunes
		1.1 search for episodes matching query (in-memory)
		2# Async start parsing episodes for retrived podcasts
		3# Return response
		3# Cache Episodes in memory (All episodes from all catalogs are indexed alreade at startup)

		PodCasts....
 			- Image (title) publisher + ---> click->PodCastDetail (+)=Subscribe
 			Action=Subscribe -> getPodCastById(PodCastCollectionId) med all episoder
 			Action=Image click
		Episodes... (Cached inmemory... only)
		    - PlayButton + Title (i) click = visa description //Om play =start player+subscribe to podcast

       API method
		getPodCast(podCastCollectionId) -> PodCast+Episodes
		    1. Look first in cache if hit return
		    2. If no hit call Itunes + parse all Episodes (Slow,
		    3. Cache result from 2

		[Client]
			1# call /api/search/query=p3&lang=ALL
			2# Display PodCast+Episodes //Quick!

