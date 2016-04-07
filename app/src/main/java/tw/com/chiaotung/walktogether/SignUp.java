package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends Activity implements View.OnClickListener,TextWatcher {
    TextView txvError,txvEmailError;
    ImageView imgError,imgEmailError;
    EditText  edtName,edtAccount,edtPassword,edtEdtPasswordRep;
    Button btConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialize
        txvError = (TextView) findViewById(R.id.txv_error);
        imgError = (ImageView) findViewById(R.id.img_error);
        txvEmailError = (TextView) findViewById(R.id.txv_email_error);
        imgEmailError = (ImageView) findViewById(R.id.img_email_error);
        txvError.setVisibility(View.INVISIBLE);
        imgError.setVisibility(View.INVISIBLE);
        txvEmailError.setVisibility(View.INVISIBLE);
        imgEmailError.setVisibility(View.INVISIBLE);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtEdtPasswordRep = (EditText)findViewById(R.id.edt_password_rep);
        btConfirm = (Button) findViewById(R.id.bt_confirm);
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
            txvEmailError.setVisibility(View.INVISIBLE);
            imgEmailError.setVisibility(View.INVISIBLE);
            ok=true;
        }
        if(!edtName.getText().toString().trim().isEmpty()&&!edtAccount.getText().toString().trim().isEmpty()
                &&!edtPassword.getText().toString().trim().isEmpty()&&!edtEdtPasswordRep.getText().toString().trim().isEmpty()){

            if(edtPassword.getText().toString().equals(edtEdtPasswordRep.getText().toString())){
                txvError.setVisibility(View.INVISIBLE);
                imgError.setVisibility(View.INVISIBLE);
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
                    Intent it = new Intent(SignUp.this, Login.class);
                    Toast.makeText(SignUp.this, "Sign Up success!", Toast.LENGTH_LONG).show();
                    startActivity(it);
                    finish();
                } else {
                    showErrorMessage();
                }
            }
        });
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
