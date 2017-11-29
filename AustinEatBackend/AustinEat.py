
#Copyright Team Wabler, UT Austin
#Author: Po-Cheng Pan, Tian Tan, Meng Lin

# [START IMPORT]
import webapp2
import global_vars as VAR
import braintree
import requests_toolbelt.adapters.appengine
from database import *
import json
import re
# [END IMPORT]

# [START LogIn]
class Login(webapp2.RequestHandler):
    def get(self):
        #todo:
        
        pass
# [END LogIn]

# [START DiscoverOrder]
class DiscoverOrder(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        pass
# [END DiscoverOrder]

# [START OrderDetail]
class OrderDetail(webapp2.RequestHandler):
    def get(self):
        pass
# [End OrderDetail]

# [START MyOrder]
class MyOrder(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        pass
# [END MyOrder]

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