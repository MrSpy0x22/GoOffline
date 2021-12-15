package pl.gooffline.presenters;

import android.content.Context;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import pl.gooffline.utils.ConfigUtil;

public class SecurityPresenter extends ConfigPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onContactUpdated();
        void onContactSaved();
        void onSecurityCodeUpdated(boolean isConfirmationField);
        void onSecurityCodeSaved();
    }

    final public static int MIN_LEN = 5;

    public SecurityPresenter(Context context) {
        super(context, Arrays.asList(
                ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SEC_ADMIN_CONTACT.getKeyName()
        ));
    }

    public String hashSHA256(String originalString) throws NoSuchAlgorithmException {
        if (originalString == null || originalString.length() < MIN_LEN) {
            throw new IllegalArgumentException("null or less that 5 characters");
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(originalString.getBytes());

        return Arrays.toString(md.digest());
    }

    public boolean compareCodeAndHash(String code , String hash) {
        if (code == null || hash == null || code.length() < 1 || hash.length() < 1) {
            return false;
        } else {
            String pinToCompareHash;
            try {
                pinToCompareHash = hashSHA256(code);
            } catch (Exception e) {
                Log.d(this.getClass().toString() , e.getMessage());
                return false;
            }

            return pinToCompareHash.equals(hash);
        }
    }
}
