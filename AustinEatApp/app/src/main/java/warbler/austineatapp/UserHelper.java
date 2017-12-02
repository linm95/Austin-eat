package warbler.austineatapp;

/**
 * Created by linm9 on 11/30/2017.
 */

public class UserHelper {
    private static String userID;
    private static String userEmail;
    private static String photoUrl;
    private static String userProperty="";
    private static String firstName;
    private static String lastName;

    public static void reset(){
        userID = null;
        userEmail = null;
        photoUrl = null;
        firstName = null;
        lastName = null;
    }
    public static boolean isSignedIn() {
        // FIXME: 10/26/17 TT: Add actual implementation
        return userID != null;
    }

    public static String getCurrentUserID() {
        // FIXME: 10/26/17 TT: Add actual implementation
        return userID;
    }

    public static void setCurrentUserID(String userID) {
        // FIXME: 10/26/17 TT: Add actual implementation
        UserHelper.userID = userID;
    }

    public static String getCurrentUserEmail() {
        // FIXME: 10/26/17 TT: Add actual implementation
        return userEmail;
    }

    public static void setCurrentUserEmail(String userEmail) {
        // FIXME: 10/26/17 TT: Add actual implementation
        UserHelper.userEmail = userEmail;
    }

    public static String getCurrentUserProperty(){
        return userProperty;
    }

    public static void setCurrentUserProperty(){
        UserHelper.userProperty = userProperty;
    }

    public static void setPhotoUrl(String photoUrl){
        UserHelper.photoUrl = photoUrl;
    }

    public static String getPhotoUrl(){
        return photoUrl;
    }

    public static String getUserIdToken() {
        // FIXME: 12/1/17 TT: Add actual implementation
        return null;
    }
    public static void setFirstName(String firstName){
        UserHelper.firstName = firstName;
    }

    public static String getFirstName(){
        return firstName;
    }

    public static void setLastName(String lastName){
        UserHelper.lastName = lastName;
    }

    public static String getLastName(){
        return lastName;
    }
}
