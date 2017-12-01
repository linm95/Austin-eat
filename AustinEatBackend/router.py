
#[START import]
import webapp2
import global_vars as VAR
import AustinEat
#[END import]

#[START APP]
app = webapp2.WSGIApplication([
    (VAR.DISCOVER_PAGE, AustinEat.DiscoverEater),
    (VAR.DISCOVER_DETAIL_PAGE, AustinEat.DiscoverDetail),
    ("/wallet", AustinEat.GetClientToken),
    ("/balance", AustinEat.GetBalance),
    ("/topup", AustinEat.IssueTransaction),
    ("/profile", AustinEat.GetProfile),
    # For Order Page
    (VAR.EATER_ORDER_PAGE, AustinEat.EaterOrder),
    (VAR.DELIVER_ORDER_PAGE, AustinEat.DeliverOrder)
], debug = True)
#[END APP]
