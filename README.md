* installation
rhc create-app tracker 'https://cartreflect-claytondev.rhcloud.com/reflect?github=smarterclayton/openshift-go-cart'
# Must stop the example process since when we change the name of the app it fails to stop the old process
rhc ssh tracker 
> gear stop
git remote add openshift ...
git push openshift master -f
