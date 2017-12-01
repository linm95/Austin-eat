# The definition of data model

# [START import]
from google.appengine.ext import ndb


# [END import]

# [START User]
class User(ndb.Model):
    first_name = ndb.StringProperty()
    last_name = ndb.StringProperty()
    name = ndb.ComputedProperty(lambda self: "{} {}".format(self.first_name, self.last_name))
    avatar_url = ndb.StringProperty()
    imageUrl = avatar_url
    email = ndb.StringProperty()

    # EaterOrDeliver
    user_property = ndb.StringProperty

    # student_number

    # paypal_account
    balance = ndb.FloatProperty()

    intro = ndb.TextProperty()
    favorite_food_styles = ndb.StringProperty(repeated=True)
    favorite_foods = ndb.StringProperty(repeated=True)

    # rate
    requester_rate = ndb.FloatProperty()
    deliveryperson_rate = ndb.FloatProperty()
    rate = ndb.ComputedProperty(lambda self: self.requester_rate + self.deliveryperson_rate)

    # reviews

    # order_history
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
    deliverList = ndb.StringProperty(repeated=True)
    #distance = ndb.FloatProperty()

    #price

    # created, pending, confirmed and fulfilled
    status = ndb.StringProperty()

    #version
# [END Order]
