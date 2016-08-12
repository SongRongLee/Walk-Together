package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUp extends Activity implements View.OnClickListener,TextWatcher {
    TextView txvError,txvEmailError;
    ImageView imgError,imgEmailError;
    EditText  edtName,edtAccount,edtPassword,edtEdtPasswordRep;
    ImageButton btConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialize
        txvError = (TextView) findViewById(R.id.txv_error);
        imgError = (ImageView) findViewById(R.id.img_error);
        txvEmailError = (TextView) findViewById(R.id.txv_email_error);
        imgEmailError = (ImageView) findViewById(R.id.img_email_error);
        txvError.setVisibility(View.GONE);
        imgError.setVisibility(View.GONE);
        txvEmailError.setVisibility(View.GONE);
        imgEmailError.setVisibility(View.GONE);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtEdtPasswordRep = (EditText)findViewById(R.id.edt_password_rep);
        btConfirm = (ImageButton) findViewById(R.id.bt_confirm);
        btConfirm.setEnabled(false);

        //set Listeners
        edtName.addTextChangedListener(this);
        edtAccount.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);
        edtEdtPasswordRep.addTextChangedListener(this);
        btConfirm.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean ok=false;
        if(!edtAccount.getText().toString().trim().isEmpty()&&!isEmailValid(edtAccount.getText().toString().trim())){
            txvEmailError.setVisibility(View.VISIBLE);
            imgEmailError.setVisibility(View.VISIBLE);
            ok=false;
        }
        else{
            txvEmailError.setVisibility(View.GONE);
            imgEmailError.setVisibility(View.GONE);
            ok=true;
        }
        if(!edtName.getText().toString().trim().isEmpty()&&!edtAccount.getText().toString().trim().isEmpty()
                &&!edtPassword.getText().toString().trim().isEmpty()&&!edtEdtPasswordRep.getText().toString().trim().isEmpty()){

            if(edtPassword.getText().toString().equals(edtEdtPasswordRep.getText().toString())){
                txvError.setVisibility(View.GONE);
                imgError.setVisibility(View.GONE);
            }
            else{
                txvError.setVisibility(View.VISIBLE);
                imgError.setVisibility(View.VISIBLE);
                ok=false;
            }
        }
        else{
            ok=false;
        }
        btConfirm.setEnabled(ok);
    }
    @Override
    public void onClick(View v) {
        String account=edtAccount.getText().toString();
        String name=edtName.getText().toString();
        String password=edtPassword.getText().toString();
        User user=new User(account,password,name);
        signUpUser(user);
    }
    public void signUpUser(User user) {
        ServerRequest request = new ServerRequest(this);

        request.signUp(user, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    //close the keyboard
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) SignUp.this.getSystemService(
                                    Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            SignUp.this.getCurrentFocus().getWindowToken(), 0);
                    //back to login
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
                    dialog.setTitle("Sign up success!");
                    dialog.setMessage("Please log in.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            headForLogIn();
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            headForLogIn();
                        }
                    });
                    dialog.show();
                } else {
                    showErrorMessage();
                }
            }
        });
    }
    public void headForLogIn(){
        Intent intent = new Intent();
        intent.setClass(SignUp.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void showErrorMessage()
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("The account has been used!");
        dialog.setPositiveButton("Ok",null);
        dialog.show();
    }
    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
