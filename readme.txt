
----   API ------
getPodCastCatalog
http://localhost:10080/PodStore/api/podCastCatalog?lang='SV'

search
http://localhost:10080/PodStore/api/podCastCatalog/search?lang=SE&query=Java

http://localhost:10080/PodStore/api/podCastCatalog/search?lang=SE&query=term=p3&entity=podcast&limit=3



Itunes Search API
https://itunes.apple.com/lookup?id=308339623


https://itunes.apple.com/search?term=p3&entity=podcast&limit=3


search
https://itunes.apple.com/search?term=p3&entity=podcast&limit=3


----
PodCastCatalog
  - on per language, List<Bundle> bundles; created + language



---
# Episode has podCastRefId = unikt id inom samma catalog som pekar på PodCast
Klient:
Sör upp podcast innom samma ctaalog
#EpisodeBuilder skalla baar ta från nerladdad PodCastBuilder! bättre perf