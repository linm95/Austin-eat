
#Copyright Team Wabler, UT Austin
#Author: Po-Cheng Pan, Tian Tan, Meng Lin

# [START IMPORT]
import webapp2
import global_vars as VAR
import database
from datetime import datetime
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
        orders = Order.query(Order.status == True).fetch()
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
