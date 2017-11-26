
#[START import]
import webapp2
import global_vars as VARS
import AustinEat
#[END import]

#[START APP]
app = webapp2.WSGIApplication([
    (VAR.DISCOVER_PAGE, AustinEat.DiscoverEater),
    (VAR.DISCOVER_DETAIL_PAGE, AustinEat.DiscoverDetail)
], debug = True)
#[END APP]
