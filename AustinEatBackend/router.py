
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
    ("/edit-profile", AustinEat.EditProfile),
    # For Order Page
    (VAR.EATER_ORDER_PAGE, AustinEat.EaterOrder),
    (VAR.DELIVER_ORDER_PAGE, AustinEat.DeliverOrder),

    (VAR.CREATE_USER, AustinEat.LogIn),
    (VAR.CREATE_ORDER, AustinEat.CreateOrder)
], debug = True)
#[END APP]
