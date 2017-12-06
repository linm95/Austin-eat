# coding=utf-8
# Copyright Team Wabler, UT Austin
# Author: Po-Cheng Pan, Tian Tan, Meng Lin

# [START IMPORT]
import webapp2
import global_vars as VAR
import database
from datetime import datetime
from datetime import timedelta
import braintree
import requests_toolbelt.adapters.appengine
from database import *
import json
import re
import math
import logging
#from google.oauth2 import id_token
#from google.auth.transport import requests
import time
import os



# [END IMPORT]


# [START AuthorizeUser]
def authorize_user(token):
    idinfo = id_token.verify_oauth2_token(token, requests.Request(), VAR.CLIENT_ID)
    if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
        raise ValueError('Wrong issuer.')
    if idinfo['hd'] != VAR.GSUITE_DOMAIN_NAME:
        raise ValueError('Wrong hosted domain.')
    return idinfo['sub']
# [END AuthorizeUser]


# [START LogIn]
class LogIn(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        email = self.request.get("email")
        #logging.info(email)
        user = User.query(User.email == email).get()
        if not user:
            #logging.info("user is none")
            user = User()
            user.first_name = self.request.get("firstName")
            user.last_name = self.request.get("lastName")
            user.avatar_url = self.request.get("url")
            user.email = email
            user.balance = 0.0
            user.intro = ""
            user.favorite_food_styles = ""
            user.favorite_foods = ""
            user.requester_rate = 0.0
            user.deliveryperson_rate = 0.0
            user.user_property = "idle"
            user.owned_orders = []
            user.put()


# [END LogIn]

# [START TimeoutDetect]
class TimeoutDetect(webapp2.RequestHandler):
    def get(self):
        orders = Order.query(Order.status != "timeout")
        if orders:
            for order in orders:
                timenow = datetime.utcnow() - timedelta(hours=6)
                if timenow < order.deadline:
                    order.status = "timeout"
                    order.put()

# [END TimeoutDetect]

# [START DiscoverOrder]
class DiscoverEater(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        lat = float(self.request.get('lat'))
        lon = float(self.request.get('lon'))
        #all_orders = Order.query().fetch()
        #logging.info(all_orders)
        orders = Order.query(ndb.OR(Order.status == "created", Order.status == "pending")).fetch()
        toSend = []
        logging.info(orders)
        for order in orders:
            logging.info("DEBUG:" + order.ownerEmail)
            user = User.query(User.email == order.ownerEmail).get()
            dic = {}
            dic["id"] = order.orderID
            dic["photoUrl"] = user.avatar_url
            dic["name"] = user.first_name
            dic["restaurant"] = order.restaurant
            dic["food"] = order.food
            dic["location"] = order.destination
            dic["deadline"] = order.due_time.strftime("%H:%M")
            dic["rating"] = user.requester_rate
            dic["price"] = order.price
            order_lat = order.destination_location.lat
            order_lon = order.destination_location.lon
            dic["distance"] = distance((lat, lon), (order_lat, order_lon))
            dic["time"] = (datetime.utcnow() - timedelta(hours=6) - order.createTime).seconds / 60.0
            toSend.append(dic)
        self.response.write(json.dumps(toSend))


# [END DiscoverOrder]


# [START distance]
def distance(loc1, loc2):
    lat1, lon1 = loc1
    lat2, lon2 = loc2
    earthRadius = 6371000.0  # m

    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2) * math.sin(dlat / 2) + math.cos(math.radians(lat1)) * math.cos(
        math.radians(lat2)) * math.sin(dlon / 2) * math.sin(dlon / 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    d = earthRadius * c
    return d
# [END distance]

# [START PullOrder]
class PullOrder(webapp2.RequestHandler):
    def post(self):
        orderID = self.request.get("id")
        deliverEmail = self.request.get("deliverEmail")
        deliver = User.query(User.email == deliverEmail).get()
        order = Order.query(Order.orderID == orderID).get()
        if(deliverEmail in order.deliverList):
            self.response.write("HasPulled")
        if(order.status == "confirmed"):
            self.response.write("HasConfirmed")

        order.status = "pending"
        order.deliverList.append(deliverEmail)
        order.put()

        deliver.owned_orders.append(orderID)
        deliver.user_property = "deliver"
        deliver.put()

# [END PullOrder]

# [START DiscoverDetail]
class DiscoverDetail(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        orderID = self.request.get("id")
        order = Order.query(Order.orderID == orderID).get()
        user = User.query(User.email == order.ownerEmail).get()
        toSend = {}
        toSend["photoUrl"] = user.avatar_url
        toSend["name"] = user.first_name
        toSend["restaurant"] = order.restaurant
        toSend["food"] = order.food
        toSend["location"] = order.destination
        toSend["deadline"] = order.due_time.strftime("%H:%M")
        toSend["rating"] = user.requester_rate
        toSend["note"] = order.note
        toSend["price"] = order.price
        toSend["resLat"] = order.restaurant_location.lat
        toSend["resLon"] = order.restaurant_location.lon
        toSend["destLat"] = order.destination_location.lat
        toSend["destLon"] = order.destination_location.lon
        toSend["creationTime"] = order.createTime.strftime("%H:%M")

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
        lon = float(self.request.get('lon'))
        email = self.request.get('email')
        orders = Order.query(Order.ownerEmail == email).fetch()
        toSend = []
        #logging.info(vars(order))
        #logging.info("DEBUG: order data: " + str(order))
        #logging.info(order.deliverList)

        for order in orders:
            if hasattr(order, "deliverList"):
                logging.info("DEBUG: deliver list is " + str(order.deliverList))
                for deliver in order.deliverList:
                    #logging.info("DEBUG: deliver is " + deliver)
                    #logging.info("DEBUG: deliver is " + str(deliver))
                    user = User.query(User.email == str(deliver)).get()
                    logging.info("DEBUG: user is  " + str(user))
                    logging.info("DEBUG: order is  " + str(order))
                    dic = {}
                    dic["id"] = order.orderID
                    dic["photoUrl"] = user.avatar_url
                    dic["name"] = user.name
                    dic["restaurant"] = order.restaurant
                    dic["food"] = order.food
                    dic["location"] = order.destination
                    dic["deadline"] = order.due_time.strftime("%H:%M")
                    dic["rating"] = user.deliveryperson_rate
                    order_lat = order.destination_location.lat
                    order_lon = order.destination_location.lon
                    dic["distance"] = distance((lat, lon), (order_lat, order_lon))
                    dic["time"] = (datetime.now() - order.createTime).seconds / 60.0
                    dic["deliver"] = deliver
                    dic["status"] = order.status
                    toSend.append(dic)
            else:
                logging.info("DEBUG: deliver list is empty.")
        self.response.write(json.dumps(toSend))


# [END EatOrder]

# [START EaterOrderDetail]
class EaterOrderDetail(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        orderID = self.request.get("id")
        deliverEmail = self.request.get("deliverEmail")
        order = Order.query(Order.orderID == orderID).get()
        user = User.query(User.email == deliverEmail).get()
        toSend = {}
        toSend["photoUrl"] = user.avatar_url
        toSend["name"] = user.name
        toSend["restaurant"] = order.restaurant
        toSend["food"] = order.food
        toSend["location"] = order.destination
        toSend["deadline"] = order.due_time.strftime("%H:%M")
        toSend["rating"] = user.deliveryperson_rate
        toSend["note"] = order.note
        toSend["price"] = order.price
        toSend["lat"] = order.destination_location.lat
        toSend["lon"] = order.destination_location.lon
        toSend["creationTime"] = order.createTime.strftime("%H:%M:%S")
        toSend["status"] = order.status
        toSend["email"] = deliverEmail

        self.response.write(json.dumps(toSend))

# [END EaterOrderDetail]

# [START ConfirmEaterOrder]
class ConfirmEaterOrder(webapp2.RequestHandler):
    # Confirm the deliver and send notification to the deliver
    # Update the order history
    def post(self):
        orderID = self.request.get("orderID")
        deliverEmail = self.request.get("deliverEmail")
        order = Order.query(Order.orderID == orderID).get()
        updatedDeliverList = [deliverEmail]
        order.status = "confirmed"
        order.deliverList = updatedDeliverList
        order.put()

# [END ConfirmEaterOrder]

# [START EaterCancelOrder]
class EaterCancelOrder(webapp2.RequestHandler):
    def post(selfs):
        def post(self):
            orderID = self.request.get("id")
            deliverEmail = self.request.get("deliverEmail")
            deliver = User.query(User.email == deliverEmail).get()
            order = Order.query(Order.orderID == orderID).get()
            # status = order.status
            deliverList = order.deliverList
            logging.info("DEBUG: deliver list before updated is " + str(deliverList))
            if deliverEmail in deliverList:
                logging.info("DEBUG: remove deliver: " + deliverEmail + " from the deliverList")
                deliverList.remove(deliverEmail)
            logging.info("DEBUG: deliver list after updated is " + str(deliverList))
            order.deliverList = deliverList
            order.put()

            # Check deliver's pulled order list. If it's empty, update the status of deliver to idle
            # testList = []
            # testList.append(deliverEmail)
            # deliverOwnedOrders = deliver.owned_orders
            logging.info("DEBUG: deliver owned order before updated is " + str(deliver.owned_orders))
            deliver.owned_orders.remove(orderID)
            logging.info("DEBUG: deliver owned order after updated is " + str(deliver.owned_orders))

            if len(deliver.owned_orders) == 0:
                deliver.status = "idle"
            deliver.put()

            # deliverOrders = Order.query().fetch()
            '''
            logging.info("DEBUG: deliver's pulled order list after cancelling is " + str(deliverOrders))
            if not deliverOrders:
                logging.info("DEBUG: deliver's pulled order list is empty, update the deliver's status.")
                deliver = User.query(User.email == deliverEmail).get()
                deliver.status = "idle"
                deliver.put()
            '''

# [END EaterCancelOrder]

# [START DeliverOrder]
class DeliverOrder(webapp2.RequestHandler):
    # Get the info of my orders (pending and confirmed)
    def post(self):
        lat = float(self.request.get('lat'))
        lon = float(self.request.get('lon'))
        deliverEmail = self.request.get('deliverEmail')

        orders = Order.query(deliverEmail == Order.deliverList).fetch()
        toSend = []
        for order in orders:
            user = User.query(User.email == order.ownerEmail).get()
            dic = {}
            dic["id"] = order.orderID
            dic["photoUrl"] = user.avatar_url
            dic["name"] = user.name
            dic["restaurant"] = order.restaurant
            dic["food"] = order.food
            dic["location"] = order.destination
            dic["deadline"] = order.due_time.strftime("%H:%M")
            dic["rating"] = user.requester_rate
            order_lat = order.destination_location.lat
            order_lon = order.destination_location.lon
            dic["distance"] = distance((lat, lon), (order_lat, order_lon))
            dic["time"] = (datetime.now() - order.createTime).seconds / 60.0
            dic["status"] = order.status
            toSend.append(dic)
        self.response.write(json.dumps(toSend))


# [END DeliverOrder]

# [START DeliverOrderDetail]
class DeliverOrderDetail(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        orderID = self.request.get("id")
        #deliverEmail = self.request.get("deliverEmail")
        order = Order.query(Order.orderID == orderID).get()
        user = User.query(User.email == order.ownerEmail).get()
        toSend = {}
        toSend["photoUrl"] = user.avatar_url
        toSend["name"] = user.first_name
        toSend["restaurant"] = order.restaurant
        toSend["food"] = order.food
        toSend["location"] = order.destination
        toSend["deadline"] = order.due_time.strftime("%H:%M")
        toSend["rating"] = user.requester_rate
        toSend["note"] = order.note
        toSend["price"] = order.price
        toSend["lat"] = order.destination_location.lat
        toSend["lon"] = order.destination_location.lon
        toSend["creationTime"] = order.createTime.strftime("%H:%M:%S")
        toSend["status"] = order.status
        toSend["email"] = order.ownerEmail
        logging.info("DEBUG: DeliverOrderDetail toSend: " + str(toSend))
        self.response.write(json.dumps(toSend))

# [END DeliverOrderDetail]

# [START DeliverCancelOrder]
class DeliverCancelOrder(webapp2.RequestHandler):
    # Confirm the eater and send notification to eater and wait to be confirmed
    # Change the order status to “waiting”
    def post(self):
        orderID = self.request.get("id")
        deliverEmail = self.request.get("deliverEmail")
        deliver = User.query(User.email == deliverEmail).get()
        order = Order.query(Order.orderID == orderID).get()
        #status = order.status
        deliverList = order.deliverList
        logging.info("DEBUG: deliver list before updated is " + str(deliverList))
        if deliverEmail in deliverList:
            logging.info("DEBUG: remove deliver: " + deliverEmail + " from the deliverList")
            deliverList.remove(deliverEmail)
        logging.info("DEBUG: deliver list after updated is " + str(deliverList))
        order.deliverList = deliverList
        order.put()

        # Check deliver's pulled order list. If it's empty, update the status of deliver to idle
        #testList = []
        #testList.append(deliverEmail)
        #deliverOwnedOrders = deliver.owned_orders
        logging.info("DEBUG: deliver owned order before updated is " + str(deliver.owned_orders))
        deliver.owned_orders.remove(orderID)
        logging.info("DEBUG: deliver owned order after updated is " + str(deliver.owned_orders))

        if len(deliver.owned_orders)==0:
            deliver.status = "idle"
        deliver.put()

        #deliverOrders = Order.query().fetch()
        '''
        logging.info("DEBUG: deliver's pulled order list after cancelling is " + str(deliverOrders))
        if not deliverOrders:
            logging.info("DEBUG: deliver's pulled order list is empty, update the deliver's status.")
            deliver = User.query(User.email == deliverEmail).get()
            deliver.status = "idle"
            deliver.put()
        '''


# [END DeliverCancelOrder]

# [START ConfirmDeliverOrder]
class ConfirmDeliverOrder(webapp2.RequestHandler):
    # Confirm the eater and send notification to eater and wait to be confirmed
    # Change the order status to “waiting”
    def post(self):
        pass


# [END ConfirmDeliverOrder]


# [START CreateOrder]
class CreateOrder(webapp2.RequestHandler):
    def get(self):
        pass

    def post(self):
        email = self.request.get("email")
        user = User.query(User.email == email).get()

        if user.user_property=="deliver":
            logging.info("DEBUG: The user is a deliver now. Hence, he/she can't create an order.")
            return
        if user.user_property == "eater":
            logging.info("DEBUG: The user is an eater now. Please complete/cancel your order first.")
            return
        # Update order info
        order = Order()
        now = datetime.utcnow() - timedelta(hours=6)
        order.createTime = now
        order.orderID = now.strftime("%Y-%m-%d %H:%M:%S.%f")
        res_lat = float(self.request.get("res_lat"))
        res_lon = float(self.request.get("res_lon"))
        order.restaurant_location = ndb.GeoPt(res_lat, res_lon)
        #logging.info(order.orderID)
        order.ownerEmail = email
        #logging.info(order.ownerEmail)
        order.price = float(self.request.get("price"))
        order.restaurant = self.request.get("restaurant")
        order.food = self.request.get("food")
        order.destination = self.request.get("location")
        lat = float(self.request.get("lat"))
        lon = float(self.request.get("lon"))
        order.destination_location = ndb.GeoPt(lat, lon)
        time = self.request.get("deadline").split(":")
        order.due_time = datetime(now.year, now.month, now.day, int(time[0]), int(time[1]))
        order.note = self.request.get("note")
        order.status = "created"
        order.deliverList = []

        order.put()
        # Update user info
        user.user_property = "eater"
        user.owned_orders.append(orderID)
        user.put()


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
    def post(self):
        token = self.request.get("idToken")
        if token:
            try:
                userid = authorize_user(token)
            except ValueError:
                # Invalid token
                self.error(401)
                return
            user = User.query(User.user_id == userid).get()
        else:
            email = self.request.get("email")
            user = User.query(User.email == email).get()

        self.response.write(user.balance)


# [END GetBalance]

# [START IssueTransaction]
class IssueTransaction(webapp2.RequestHandler):
    def post(self):
        # Use the App Engine Requests adapter. This makes sure that Requests uses
        # URLFetch.
        requests_toolbelt.adapters.appengine.monkeypatch()

        token = self.request.get("idToken")
        if token:
            try:
                userid = authorize_user(token)
            except ValueError:
                # Invalid token
                self.error(401)
                return
            user = User.query(User.user_id == userid).get()
        else:
            email = self.request.get("email")
            user = User.query(User.email == email).get()

        gateway = braintree.BraintreeGateway(access_token=VAR.paypal_access_token)
        result = gateway.transaction.sale({
            "amount": self.request.get("amount"),
            "merchant_account_id": "USD",
            "payment_method_nonce": self.request.get("payment_method_nonce"),
            # "shipping": {
            #    "first_name": self.request.get("first_name"),
            #    "last_name": self.request.get("last_name"),
            #    "street_address": self.request.get("street_address"),
            #    "locality": self.request.get("locality"),
            #    "region": self.request.get("region"),
            #    "postal_code": self.request.get("postal_code"),
            #    "country_code_alpha2": self.request.get("country")
            # },
        })

        if result.is_success:
            user.transaction_history.append(result.transaction.id)
            user.balance += result.transaction.amount

            user.put()
            if os.getenv('SERVER_SOFTWARE', '').startswith('Google App Engine/'):
                pass
            else:
                time.sleep(3)

            self.response.write(user.balance)
        else:
            self.response.write(result.message)
            self.set_status(500)


# [END IssueTransaction]


# [START GetProfile]
class GetProfile(webapp2.RequestHandler):
    def post(self):
        token = self.request.get("idToken")
        if token:
            try:
                userid = authorize_user(token)
            except ValueError:
                # Invalid token
                self.error(401)
                return
            user = User.query(User.user_id == userid).get()
        else:
            email = self.request.get("email")
            user = User.query(User.email == email).get()

        ret = {
            "first_name": user.first_name,
            "last_name": user.last_name,
            "avatar_url": user.avatar_url,
            "intro": user.intro,
            "favorite_food_styles": user.favorite_food_styles,
            "favorite_foods": user.favorite_foods,
            "requester_rate": user.requester_rate,
            "deliveryperson_rate": user.deliveryperson_rate
        }
        self.response.write(json.dumps(ret))


# [END GetProfile]


# [START EditProfile]
class EditProfile(webapp2.RequestHandler):
    def post(self):
        token = self.request.get("idToken")
        if token:
            try:
                userid = authorize_user(token)
            except ValueError:
                # Invalid token
                self.error(401)
                return
            user = User.query(User.user_id == userid).get()
        else:
            email = self.request.get("email")
            user = User.query(User.email == email).get()

        user.first_name = self.request.get("first_name")
        user.last_name = self.request.get("last_name")
        user.intro = self.request.get("intro")

        user.favorite_food_styles = self.request.get("favorite_food_styles")
        user.favorite_foods = self.request.get("favorite_foods")

        user.put()
        if os.getenv('SERVER_SOFTWARE', '').startswith('Google App Engine/'):
            pass
        else:
            time.sleep(3)


# [END EditProfile]


# [START GetOrderHistory]
class GetOrderHistory(webapp2.RequestHandler):
    def get(self):
        pass

# [END GetOrderHistory]

# [START SetUserProperty]
class GetUserProperty(webapp2.RequestHandler):
    def get(self):
        email = self.request.get("email")
        user = User.query(User.email == email).get()

        if user is not None:
            logging.info("DEBUG: user is " + user.email)

            if user.user_property is not None:
                logging.info("DEBUG: user_property is " + user.user_property)

            self.response.write(user.user_property)
        else:
            logging.info("DEBUG: user is none")


# [END SetUserProperty]

# [START MySecretCreateOrder]
class MySecretCreateOrder(webapp2.RequestHandler):
    def get(self):
        status = self.request.get("status")
        email = self.request.get("ownerEmail")

        order = Order()
        order.status = status
        now = datetime.utcnow() - timedelta(hours=6)
        order.createTime = now
        order.orderID = now.strftime("%Y-%m-%d %H:%M:%S.%f")
        order.ownerEmail = email
        order.due_time = datetime(now.year, now.month, now.day, 23, 42)
        order.restaurant_location = ndb.GeoPt(12.3, 35.6)
        order.destination_location = ndb.GeoPt(35.4, 22.5)

        order.put()
        if os.getenv('SERVER_SOFTWARE', '').startswith('Google App Engine/'):
            pass
        else:
            time.sleep(3)

# [END MySecretCreateOrder]
