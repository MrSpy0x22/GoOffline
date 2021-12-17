package pl.gooffline.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.security.NoSuchAlgorithmException;

import pl.gooffline.R;
import pl.gooffline.presenters.SecurityPresenter;
import pl.gooffline.utils.ConfigUtil;

public class SecurityFragment extends Fragment implements SecurityPresenter.View {
    private SecurityPresenter presenter;
    private EditText editOldPassword;
    private EditText editNewPassword;
    private EditText editNewPasswordConfirm;
    private Button buttonSaveCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_security, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new SecurityPresenter(requireContext());

        editOldPassword = view.findViewById(R.id.sec_old_password);
        editNewPassword = view.findViewById(R.id.sec_new_password);
        editNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onSecurityCodeUpdated(false);
            }
        });

        editNewPasswordConfirm = view.findViewById(R.id.sec_new_password_confirm);
        editNewPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onSecurityCodeUpdated(true);
            }
        });

        buttonSaveCode = view.findViewById(R.id.sec_save_code);
        buttonSaveCode.setOnClickListener(e -> this.onSecurityCodeSaved());
    }

    @Override
    public void onSecurityCodeUpdated(boolean isConfirmationField) {
        if (isConfirmationField) {

        } else {

        }
    }

    @Override
    public void onSecurityCodeSaved() {
        String oldPassword = editOldPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();
        String newPasswordConfirm = editNewPasswordConfirm.getText().toString();
        String oldHash = presenter.getConfigValue(ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD);

        if (oldHash.length() == 0 || presenter.compareCodeAndHash(oldPassword , oldHash)) {
            if (newPassword.length() >= SecurityPresenter.MIN_LEN && newPassword.equals(newPasswordConfirm)) {
                try {
                    String newHash = presenter.hashSHA256(newPassword);
                    presenter.setConfigValue(ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD , newHash);

                    editOldPassword.setText("");
                    editNewPassword.setText("");
                    editNewPasswordConfirm.setText("");
                    prepareMessageDialog("Sukces" , "Hasło zostało zmienione - zapamiętaj je!").show();
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(requireContext(), "Błąd aplikacji!", Toast.LENGTH_SHORT).show();
                }
            } else {
                prepareMessageDialog("Popraw dane" , "Hasła nie skłąda się z minimum " +
                        SecurityPresenter.MIN_LEN + " lub nie pasują do siebie.").show();
            }
        } else {
            prepareMessageDialog("Błąd" , "Stare hasło jest niepoprawne.").show();
        }
    }

    private AlertDialog prepareMessageDialog(String caption , String text) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(caption)
                .setMessage(text)
                .setCancelable(true)
                .setPositiveButton("OK" , (dialogInterface, i) -> {})
                .create();
    }
}
