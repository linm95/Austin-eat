#The definition of data model

# [START import]
from google.appengine.ext import ndb
# [END import]

# [START User]
class User(ndb.model):
    name = ndb.StringProperty
    email
    student_number
    paypal_account

    rate
    reviews

    order_history
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
