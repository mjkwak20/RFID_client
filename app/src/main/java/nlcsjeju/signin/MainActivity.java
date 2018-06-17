package nlcsjeju.signin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;

import nlcsjeju.signin.Model.Student;
import nlcsjeju.signin.Model.Location;

public class MainActivity extends AppCompatActivity {

    int location;
    EditText uid;
    View studentInfo;
    TextView studentName;
    TextView previousLocation;
    TextView currentLocation;

    static MainActivityUIHandler uiHandler;

    TextWatcher uidWatcher;

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    static class UIAction{
        final static int HIDE_STUDENT_INFO = 10;
    }

    static class MainActivityUIHandler extends Handler{
        WeakReference<MainActivity> mainActivity;
        MainActivityUIHandler(MainActivity activity){
            mainActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mainActivity.get();

            switch(msg.what){
                case UIAction.HIDE_STUDENT_INFO:
                    activity.studentInfo.setVisibility(View.INVISIBLE);
                    break;
                default:
                    Util.getInstance().log("SWITCH EXCEIPTION");
                    break;
            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        Util.getInstance().log("Activity oncreate");
        super.onCreate(savedInstanceState);

        if(SelectLocation.location.equals(null)){

        }


        location = 71;

        uid = (EditText) findViewById(R.id.uid);
        uid.setInputType(InputType.TYPE_NULL);
        studentInfo = findViewById(R.id.studentInfo);
        studentName = (TextView) findViewById(R.id.name);
        previousLocation = (TextView) findViewById(R.id.previousLocation);
        currentLocation = (TextView) findViewById(R.id.currentLocation);

        uiHandler = new MainActivityUIHandler(this);

        try {
            currentLocation.setText(currentLocation.getText().toString().replace("%s", getResources().getString(R.string.class.getField("location_" + location).getInt(null))));
        }catch(Exception e){
            new AlertDialog.Builder(this)
                    .setTitle("Oops!")
                    .setMessage("An error has occurred. Please contact the system administrator for assistance.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAndRemoveTask();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            finishAndRemoveTask();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        this.uidWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if(str.length() == 8){ // if string length is 8:
                    signinRequest(str); // request sign in
                    MainActivity.this.uid.setText("");
                }
            }
        };

        uid.addTextChangedListener(uidWatcher);
    }




    @Override
    protected void onResume() {
        super.onResume();
        this.uid.requestFocus();
    }

    private void signinRequest(String id){
        final DatabaseReference dbReference = database.getReference("students/"+id);
        Util.getInstance().log("Signin Request with UID : "+id);

        final ValueEventListener studentInfo = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                if(Objects.isNull(student)){ // invalid UID
                    Toast.makeText(MainActivity.this, "ID card unrecognised.", Toast.LENGTH_LONG).show();
                }else {
                    // update location value
                    try {
                        studentName.setText(student.getName());
                        String previous = getResources().getString(R.string.previous_location)+" : "+getResources().getString(R.string.class.getField("location_"+student.getLocation()).getInt(null));
                        previousLocation.setText(previous);

                        // remove all previous callbacks
                        uiHandler.removeCallbacksAndMessages(null);

                        dbReference.child("location").setValue(location);

                        MainActivity.this.studentInfo.setVisibility(View.VISIBLE);

                        uiHandler.sendEmptyMessageDelayed(UIAction.HIDE_STUDENT_INFO, 6000);
                    }catch(Exception e){
                        Util.getInstance().log("Exception occurred");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.getInstance().log("Database request failed : "+databaseError.getMessage());
            }
        };

        dbReference.addListenerForSingleValueEvent(studentInfo);
    }


    public void onSignout(View view){
        // initialise RFID reader
        Intent signoutIntent = new Intent(this, SelectLocation.class);
        startActivity(signoutIntent);
    }
}
