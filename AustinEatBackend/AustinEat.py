
#Copyright Team Wabler, UT Austin
#Author: Po-Cheng Pan, Tian Tan, Meng Lin

# [START IMPORT]
import webapp2
import global_vars as VAR
import database
from datetime import datetime
import braintree
import requests_toolbelt.adapters.appengine
from database import *
import json
import re
import math
# [END IMPORT]

# [START LogIn]
class Login(webapp2.RequestHandler):
    def get(self):
        #todo:

        pass
# [END LogIn]

# [START DiscoverOrder]
class DiscoverEater(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        lat = float(self.request.get('lat'))
        lon = float(self.request.get('lng'))
        orders = Order.query(Order.status == "created" or Order.status == "pending").fetch()
        toSend = []
        for order in orders:
            user = User.query(User.email == Order.ownerEmail)
            dic = {}
            dic["id"] = order.orderID
            dic["photoUrl"] = user.imageUrl
            dic["name"] = user.name
            dic["restaurant"] = order.restaurant
            dic["food"] = order.food
            dic["location"] = order.destination
            dic["deadline"] = order.due_time.strftime("%H:%M")
            dic["rating"] = user.rate
            order_lat = order.destination_location.lat
            order_lon = order.destination_location.lon
            dic["distance"] = distance((lat, lon), (order_lat, order_lon))
            dic["time"] = (datetime.now() - order.createTime).seconds / 60.0
            toSend.append(dic)
        self.response.write(json.dumps(toSend))
# [END DiscoverOrder]

# [START distance]
def distance(loc1, loc2):
    lat1, lon1 = loc1
    lat2, lon2 = loc2
    earthRadius = 6371000.0 #m

    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2) * math.sin(dlat / 2) + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) * math.sin(dlon / 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    d = earthRadius * c
    return d
# [END distance]

# [START DiscoverDetail]
class DiscoverDetail(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        orderID = self.request.get("id")
        order = Order.query(Order.orderID == orderID).get()
        user = User.query(User.email == order.ownerEmail).get()
        toSend = {}
        toSend["photoUrl"] = user.imageUrl
        toSend["name"] = user.name
        toSend["restaurant"] = order.restaurant
        toSend["food"] = order.food
        toSend["location"] = order.destination
        toSend["deadline"] = order.due_time.strftime("%H:%M")
        toSend["rating"] = user.rate
        toSend["note"] = order.note
        toSend["lat"] = order.destination_location.lat
        toSend["lon"] = order.destination_location.lon
        toSend["creationTime"] = order.createTime.strftime("%H:%M:%S")

        self.response.write(json.dumps(toSend))
# [END DiscoverDetail]


# [START MyOrder]
# Redirect to EaterOrder or DeliverOrder
class MyOrder(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        user_email = self.request.get("email")
        user = User.query(User.email == user_email).get()
        if user.user_property == "eater":
            return webapp2.redirect('/eater-order')
        elif user.user_property == "deliver":
            return webapp2.redirect('/deliver-order')
        else:
            print "INFO: User Property is neither deliver nor eater!"
            pass
# [END MyOrder]

# [START EatOrder]
# Redirect to EaterOrder or DeliverOrder
class EaterOrder(webapp2.RequestHandler):
    # Get the info of my orders
    def post(self):
        lat = float(self.request.get('lat'))
        lon = float(self.request.get('lng'))
        email = self.request.get('email')
        order = Order.query(Order.status == email).fetch()
        toSend = []

        for deliver in order.deliverList:
            user = User.query(User.email == deliver)
            dic = {}
            dic["id"] = order.orderID
            dic["photoUrl"] = user.imageUrl
            dic["name"] = user.name
            dic["restaurant"] = order.restaurant
            dic["food"] = order.food
            dic["location"] = order.destination
            dic["deadline"] = order.due_time.strftime("%H:%M")
            dic["rating"] = user.rate
            order_lat = order.destination_location.lat
            order_lon = order.destination_location.lon
            dic["distance"] = distance((lat, lon), (order_lat, order_lon))
            dic["time"] = (datetime.now() - order.createTime).seconds / 60.0
            toSend.append(dic)

        self.response.write(json.dumps(toSend))
# [END EatOrder]

# [START ConfirmEaterOrder]
class ConfirmEaterOrder(webapp2.RequestHandler):
    # Confirm the deliver and send notification to the deliver
    # Update the order history
    def post(self):
        pass
# [END ConfirmEaterOrder]

# [START DeliverOrder]
class DeliverOrder(webapp2.RequestHandler):
    # Get the info of my orders (pending and confirmed)
    def post(self):
        lat = float(self.request.get('lat'))
        lon = float(self.request.get('lng'))
        deliver = float(self.request.get('deliver'))

        orders = Order.query(deliver == Order.deliverList).fetch()
        toSend = []
        for order in orders:
            user = User.query(User.email == order.ownerEmail)
            dic = {}
            dic["id"] = order.orderID
            dic["photoUrl"] = user.imageUrl
            dic["name"] = user.name
            dic["restaurant"] = order.restaurant
            dic["food"] = order.food
            dic["location"] = order.destination
            dic["deadline"] = order.due_time.strftime("%H:%M")
            dic["rating"] = user.rate
            order_lat = order.destination_location.lat
            order_lon = order.destination_location.lon
            dic["distance"] = distance((lat, lon), (order_lat, order_lon))
            dic["time"] = (datetime.now() - order.createTime).seconds / 60.0
            dic["status"] = order.status
            toSend.append(dic)
        self.response.write(json.dumps(toSend))
# [END DeliverOrder]

# [START ConfirmDeliverOrder]
class ConfirmDeliverOrder(webapp2.RequestHandler):
    # Confirm the eater and send notification to eater and wait to be confirmed
    # Change the order status to “waiting”
    def post(self):
        pass
# [END ConfirmDeliverOrder]

# [START EaterOrderDetail]
class EaterOrderDetail(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        orderID = self.request.get("id")
        order = Order.query(Order.orderID == orderID).get()
        user = User.query(User.email == order.ownerEmail).get()
        toSend = {}
        toSend["photoUrl"] = user.imageUrl
        toSend["name"] = user.name
        toSend["restaurant"] = order.restaurant
        toSend["food"] = order.food
        toSend["location"] = order.destination
        toSend["deadline"] = order.due_time.strftime("%H:%M")
        toSend["rating"] = user.rate
        toSend["note"] = order.note
        toSend["lat"] = order.destination_location.lat
        toSend["lon"] = order.destination_location.lon
        toSend["creationTime"] = order.createTime.strftime("%H:%M:%S")

        self.response.write(json.dumps(toSend))
# [END EaterOrderDetail]

# [START DeliverOrderDetail]
class DeliverOrderDetail(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        orderID = self.request.get("id")
        order = Order.query(Order.orderID == orderID).get()
        user = User.query(User.email == order.ownerEmail).get()
        toSend = {}
        toSend["photoUrl"] = user.imageUrl
        toSend["name"] = user.name
        toSend["restaurant"] = order.restaurant
        toSend["food"] = order.food
        toSend["location"] = order.destination
        toSend["deadline"] = order.due_time.strftime("%H:%M")
        toSend["rating"] = user.rate
        toSend["note"] = order.note
        toSend["lat"] = order.destination_location.lat
        toSend["lon"] = order.destination_location.lon
        toSend["creationTime"] = order.createTime.strftime("%H:%M:%S")

        self.response.write(json.dumps(toSend))
# [END DeliverOrderDetail]

# [START CreateOrder]
class CreateOrder(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        order = Order()
        order.createTime = datetime.now()
        order.orderID = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")
        order.ownerEmail = self.request.get("email")
        order.food = self.request.get("food")
        order.destination = self.request.get("location")
        order.due_time = self.request.get("deadline")
        order.note = self.request.get("note")

        order.put()
# [END CreateOrder]
# [START MyProfile]
class MyProfile(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        pass
# [END MyProfile]

# [START GetClientToken]
class GetClientToken(webapp2.RequestHandler):
    def get(self):
        # Use the App Engine Requests adapter. This makes sure that Requests uses
        # URLFetch.
        requests_toolbelt.adapters.appengine.monkeypatch()

        gateway = braintree.BraintreeGateway(access_token=VAR.paypal_access_token)
        client_token = gateway.client_token.generate()
        self.response.write(client_token)
# [END GetClientToken]

# [START GetBalance]
class GetBalance(webapp2.RequestHandler):
    def get(self):
        # fixme: TT add implementation
        self.response.write("42.50")
# [END GetBalance]

# [START IssueTransaction]
class IssueTransaction(webapp2.RequestHandler):
    def post(self):
        # Use the App Engine Requests adapter. This makes sure that Requests uses
        # URLFetch.
        requests_toolbelt.adapters.appengine.monkeypatch()

        gateway = braintree.BraintreeGateway(access_token=VAR.paypal_access_token)
        result = gateway.transaction.sale({
            "amount": self.request.get("amount"),
            "merchant_account_id": "USD",
            "payment_method_nonce": self.request.get("payment_method_nonce"),
            #"shipping": {
            #    "first_name": self.request.get("first_name"),
            #    "last_name": self.request.get("last_name"),
            #    "street_address": self.request.get("street_address"),
            #    "locality": self.request.get("locality"),
            #    "region": self.request.get("region"),
            #    "postal_code": self.request.get("postal_code"),
            #    "country_code_alpha2": self.request.get("country")
            #},
        })

        if result.is_success:
            self.response.write("success")
        else:
            self.error(500)
# [END IssueTransaction]


# [START GetProfile]
class GetProfile(webapp2.RequestHandler):
    def get(self):
        user_email = self.request.get("email")
        user = User.query(User.email == user_email).get()
        self.response.write(json.dumps(user.to_dict()))
# [END GetProfile]


# [START EditProfile]
class EditProfile(webapp2.RequestHandler):
    def post(self):
        user_email = self.request.get("email")
        user = User.query(User.email == user_email).get()

        user.first_name = self.request.get("first_name")
        user.last_name = self.request.get("last_name")
        user.intro = self.request.get("intro")

        favorite_food_styles_str = self.request.get("favorite_food_styles")
        favorite_food_styles_list = re.split(",\s*", favorite_food_styles_str)
        user.favorite_food_styles = favorite_food_styles_list

        favorite_foods_str = self.request.get("favorite_foods")
        favorite_foods_list = re.split(",\s*", favorite_foods_str)
        user.favorite_foods = favorite_foods_list

        user.put()
# [END EditProfile]


# [START GetOrderHistory]
class GetOrderHistory(webapp2.RequestHandler):
    def get(self):
        user_email = self.request.get("email")
        user = User.query(User.email == user_email).get()
        orders = Order.query(ancestor=user.key()).order(-Order.date).fetch()
        orders_dict = map(lambda x: x.to_dict(), orders)
        self.response.write(json.dumps(orders_dict))
# [END GetOrderHistory]

# [START app]
app = webapp2.WSGIApplication([
    (VAR.WALLET_URL, GetClientToken),
    ("/balance", GetBalance),
    ("/topup", IssueTransaction),
], debug=True)
# [END app]
