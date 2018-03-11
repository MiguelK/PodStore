Version 1.1
- language suppurt in search query
- fix for simplecast.com

Version 2.5.0 - October 2017
============================


Local files will be here /tmp/POD_DATA_HOME/PodCastCatalogVersions

##### Server
WildFly 10 administrator added.  Please make note of these credentials:

Username: adminEPFdZJx
Password: jRmngM53mG6u

run 'rhc port-forward podstore' to access the web admin area on port 9990.

Open Shift
1# Kopiera war filen till servern:
scp scp /Users/miguelkrantz/Documents/temp/PodStore.war 586e521b7628e1e473000140@podstore-itemstore.rhcloud.com:///var/lib/openshift/586e521b7628e1e473000140/wildfly/standalone/deployments

* Login OpenShift
ssh 586e521b7628e1e473000140@podstore-itemstore.rhcloud.com

* OPENSHIFT_DATA_DIR (All persistent data skall lagras här)
 cd /var/lib/openshift/586e521b7628e1e473000140/app-root/data

* ALIAS
cd /var/lib/openshift/586e521b7628e1e473000140/wildfly/standalone/log/


tail -f /var/lib/openshift/586e521b7628e1e473000140/wildfly/standalone/log/server.log

Check file sizes
du -h * | sort -rh | head -50

rm -rf .* *   (Tar bort alla file/dir i samma katalog)

To check if you're hitting the inode limit use quota command:
quota -s




More memory JVM OpenShidt
https://developers.openshift.com/servers/tomcat/jvm-memory.html


###### Admin  ######
http://localhost:10080/PodStore

###### API #######
[podCastCatalog]
http://localhost:10080/PodStore/api/podCastCatalog?lang='SV'
http://localhost:10080/PodStore/api/podCastCatalog/search?lang=SE&query=Java
http://localhost:10080/PodStore/api/podCastCatalog/search?lang=SE&query=p3

api/podCast
http://localhost:10080/PodStore/api/podCast?id='podCastCollectionId'

api/subscriber
GET http://localhost:10080/PodStore/api/subscriber   (Get status)

api/podCastSubscription
GET http://localhost:10080/PodStore/api/podCastSubscription   (Get status)

api/jsonfilejsonfile   (get productcatlog JSON ZIP file)
http://localhost:10080/PodStore/api/jsonfile?lang=SWE

http://localhost:10080/PodStore/api/jsonfile?lang=US

###########################################################################


Itunes Search API
https://itunes.apple.com/lookup?id=308339623
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

