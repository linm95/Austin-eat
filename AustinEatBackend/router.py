
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
    # For Eater Order Page
    (VAR.EATER_ORDER_PAGE, AustinEat.EaterOrder),
    (VAR.EATER_ORDER_DETAIL_PAGE, AustinEat.EaterOrderDetail),
    (VAR.CONFIRM_EATER_ORDER_PAGE, AustinEat.ConfirmEaterOrder),
    (VAR.EATER_COMPLETE_ORDER_PAGE, AustinEat.EaterCompleteOrder),
    (VAR.EATER_CANCEL_ORDER_PAGE, AustinEat.EaterCancelOrder),
    # For Deliver Order Page
    (VAR.DELIVER_ORDER_PAGE, AustinEat.DeliverOrder),
    (VAR.DELIVER_ORDER_DETAIL_PAGE, AustinEat.DeliverOrderDetail),
    (VAR.DELIVER_CANCEL_ORDER_PAGE, AustinEat.DeliverCancelOrder),

    (VAR.GET_USER_PROPERTY, AustinEat.GetUserProperty),

    (VAR.TIMEOUT_DETECT, AustinEat.TimeoutDetect),
    (VAR.PULL_ORDER, AustinEat.PullOrder),
    (VAR.CREATE_USER, AustinEat.LogIn),
    (VAR.CREATE_ORDER, AustinEat.CreateOrder),

    # for debug
    ("/debug-add-order", AustinEat.MySecretCreateOrder),
], debug = True)
#[END APP]
