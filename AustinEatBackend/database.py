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
    email = ndb.StringProperty()
    user_id = ndb.StringProperty()

    owned_orders = ndb.StringProperty(repeated=True)

    # EaterOrDeliver
    user_property = ndb.StringProperty()

    # student_number

    # paypal_account
    balance = ndb.FloatProperty()
    transaction_history = ndb.StringProperty(repeated=True)

    intro = ndb.TextProperty()
    favorite_food_styles = ndb.StringProperty()
    favorite_foods = ndb.StringProperty()

    # rate
    requester_rate = ndb.FloatProperty()
    requester_rate_num = ndb.IntegerProperty()
    deliveryperson_rate = ndb.FloatProperty()
    deliveryperson_rate_num = ndb.IntegerProperty()

    # reviews

    # order_history
# [END User]

# [START Order]
class Order(ndb.Model):
    createTime = ndb.DateTimeProperty()
    orderID = ndb.StringProperty()
    ownerEmail = ndb.StringProperty()
    restaurant = ndb.StringProperty()
    restaurant_location = ndb.GeoPtProperty()
    food = ndb.StringProperty()
    destination = ndb.StringProperty()
    destination_location = ndb.GeoPtProperty()
    due_time = ndb.DateTimeProperty()
    note = ndb.StringProperty()
    deliverList = ndb.StringProperty(repeated=True)
    #deliverList_count = ndb.ComputedProperty(lambda e: len(e.deliverList))
    #distance = ndb.FloatProperty()
    price = ndb.FloatProperty()
    #price
    chatChannel = ndb.StringProperty()


    # created, pending, confirmed and fulfilled
    status = ndb.StringProperty()

    # rate
    requester_rate = ndb.FloatProperty()
    deliveryperson_rate = ndb.FloatProperty()

    #version
# [END Order]
