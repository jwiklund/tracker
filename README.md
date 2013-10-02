# Tracker
A simple tracker that converts current state of pages into rss feeds that can be tracked by a rss reader.
Built using golang and configured to be deployable in openshift (no state for now)

# Installation
```
rhc create-app tracker 'https://cartreflect-claytondev.rhcloud.com/reflect?github=smarterclayton/openshift-go-cart'
# Must stop the example process since when we change the name of the app it fails to stop the old process
rhc ssh tracker gear stop
git remote add openshift ...
git push openshift master -f
```
