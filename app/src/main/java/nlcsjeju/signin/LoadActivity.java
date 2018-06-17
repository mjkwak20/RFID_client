package nlcsjeju.signin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import nlcsjeju.signin.Model.Location;
import nlcsjeju.signin.Model.Meta;

public class LoadActivity extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    TextView loadingLabel;
    File meta;
    static LoadActivityUIHandler uiHandler;

    static class UIAction{
        static final int LOAD_FINISHED = 10;
    }

    static class LoadActivityUIHandler extends Handler{
        WeakReference<LoadActivity> loadActivity;
        LoadActivityUIHandler(LoadActivity activity){
            loadActivity = new WeakReference<LoadActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoadActivity activity = loadActivity.get();
            switch(msg.what){
                case UIAction.LOAD_FINISHED:
                    // called when all resources are loaded
                    try {
                        String json = activity.readFromFile(activity.meta);
                        activity.parseJSON(json); // parse JSON and save array
                        Intent main = new Intent(activity, MainActivity.class);
                        activity.startActivity(main); // launch MainActivity
                        activity.finish(); // close LoadActivity

                    }catch(IOException e){
                        Util.getInstance().log(e.toString());
                    }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        loadingLabel = findViewById(R.id.loading_description);
        uiHandler = new LoadActivityUIHandler(this);

        // check for meta.json updates
        fetchLocations();
    }

    private void downloadMetaFile(){
        Util.getInstance().log("Downloading meta.json...");
        loadingLabel.setText(getResources().getString(R.string.updating));
        StorageReference reference = storage.getReference().child("meta.json");
        // Create reference to the local directory
        meta = new File(LoadActivity.this.getFilesDir(), "meta.json");
        reference.getFile(meta).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // file saved locally
                Util.getInstance().log("File downloaded : "+meta.toString());
                uiHandler.sendEmptyMessage(UIAction.LOAD_FINISHED);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Util.getInstance().log(e.toString());
            }
        });
    }

    private void fetchLocations(){
        StorageReference reference = storage.getReference().child("meta.json");
        meta = new File(LoadActivity.this.getFilesDir(), "meta.json");
        if(!meta.exists()){ // if the meta.json file does not exist
            downloadMetaFile(); // download
        }else {
            reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    Util.getInstance().log("Storage Metadata retrieved");
                    if(storageMetadata.getUpdatedTimeMillis() > meta.lastModified()){
                        downloadMetaFile();
                    }else{
                        uiHandler.sendEmptyMessage(UIAction.LOAD_FINISHED);
                    }
                }
            });
        }
    }

    private String readFromFile(File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();
        while(true){
            final String line = reader.readLine();
            if(line == null){
                break;
            }
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }

    private void parseJSON(String json){
        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse(json);
        Gson gson = new Gson();

        Meta meta = gson.fromJson(json, Meta.class);
        SelectLocation.location = new Location[meta.getLocation().size()];
        meta.getLocation().toArray(SelectLocation.location);
        Util.getInstance().log(SelectLocation.location[0].getName());
//        SelectLocation.location = (Location[]) meta.getLocation().toArray();

    }
}
