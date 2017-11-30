package warbler.austineatapp;

/**
 * Created by linm9 on 11/30/2017.
 */

public class UserHelper {
    private static String userID;
    private static String userEmail;
    private static String photoUrl;

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

    public static void setPhotoUrl(String photoUrl){
        UserHelper.photoUrl = photoUrl;
    }

    public static String getPhotoUrl(){
        return photoUrl;
    }
}
