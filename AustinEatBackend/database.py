#The definition of data model

# [START import]
from google.appengine.ext import ndb
# [END import]

# [START User]
class User(ndb.model):
    name = ndb.StringProperty()
    imageUrl = ndb.StringProperty()
    email = ndb.StringProperty()
    #student_number
    #paypal_account

    rate = ndb.FloatProperty()
    #reviews

    #order_history
# [END User]

# [START Order]
class Order(ndb.model):
    createTime = ndb.DateTimeProperty()
    orderID = ndb.StringProperty()
    ownerEmail = ndb.StringProperty()
    food = ndb.StringProperty()
    destination = ndb.StringProperty()
    destination_location = ndb.GeoPtProperty()
    due_time = ndb.DateTimeProperty()
    note = ndb.StringProperty()
    #distance = ndb.FloatProperty()

    #price

    status = ndb.BooleanProprtty()

    #version
# [END Order]
