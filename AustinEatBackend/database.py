# The definition of data model

# [START import]
from google.appengine.ext import ndb


# [END import]

# [START User]
class User(ndb.Model):
    first_name = ndb.StringProperty()
    last_name = ndb.StringProperty()
    avatar_url = ndb.StringProperty()
    email = ndb.StringProperty()

    # student_number

    # paypal_account
    balance = ndb.FloatProperty()

    intro = ndb.TextProperty()
    favorite_food_styles = ndb.StringProperty(repeated=True)
    favorite_foods = ndb.StringProperty(repeated=True)

    # rate
    requester_rate = ndb.FloatProperty()
    deliveryperson_rate = ndb.FloatProperty()

    # reviews

    # order_history


# [END User]

# [START Order]
class Order(ndb.model):
    owner
    food
    destination
    due_time
    price

    status

    version

# [END Order]
