package warbler.austineatapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;

/**
 * Created by doublsky on 11/18/17.
 */

@RunWith(AndroidJUnit4.class)
public class PaypalInstrumentedTest {
    @Rule
    public ActivityTestRule<WalletActivity> activityTestRule =
            new ActivityTestRule<WalletActivity>(WalletActivity.class);

    @Test
    public void BraintreeFragmentNotNull() throws Exception {
        WalletActivity walletActivity = activityTestRule.getActivity();
        String url = walletActivity.getString(R.string.root_url)
                + walletActivity.getString(R.string.wallet_url);
        String clientToken = walletActivity.GetClientToken(url);
        Object braintreeFragment = walletActivity.CreateBraintreeFragment(clientToken);
        assertNotNull(braintreeFragment);
    }
}
