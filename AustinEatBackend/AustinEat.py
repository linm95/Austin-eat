
#Copyright Team Wabler, UT Austin
#Author: Po-Cheng Pan, Tian Tan, Meng Lin

# [START IMPORT]
import webapp2
import global_vars as VAR
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
        import braintree
        import requests_toolbelt.adapters.appengine

        # Use the App Engine Requests adapter. This makes sure that Requests uses
        # URLFetch.
        requests_toolbelt.adapters.appengine.monkeypatch()

        gateway = braintree.BraintreeGateway(access_token=VAR.paypal_access_token)
        client_token = gateway.client_token.generate()
        self.response.write(client_token)
# [END GetClientToken]

# [START app]
app = webapp2.WSGIApplication([
    (VAR.WALLET_URL, GetClientToken),
], debug=True)
# [END app]