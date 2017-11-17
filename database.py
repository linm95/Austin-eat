#The definition of data model

# [START import]
from google.appengine.ext import ndb
# [END import]

# [START User]
class User(ndb.model):
    name = ndb.StringProperty
# [END User]
