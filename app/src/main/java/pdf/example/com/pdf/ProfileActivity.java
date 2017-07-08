package pdf.example.com.pdf;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.List;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfAWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.itextpdf.text.Annotation.FILE;


public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ProfileActivity";
    private GoogleApiClient mGoogleApiClient;

    public static class FileCreate extends DialogFragment {



        private ArrayList<String> GetFiles(String DirectoryPath) {
            ArrayList<String> MyFiles = new ArrayList<String>();
            File f = new File(Environment.getExternalStorageDirectory().toString());

            f.mkdirs();
            File[] files = f.listFiles();
            if (files.length == 0)
                return null;
            else {
                for (int i=0; i<files.length; i++)
                    MyFiles.add(files[i].getName());
            }

            return MyFiles;
        }


        private String[] ReadPDFDirectory()
        {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File f = new File(filepath + "/PDFs" );
            File[] files=f.listFiles();
            ArrayList<String> fileList = new ArrayList<>();
            for(int i=0; i<files.length; i++)
            {
                File file = files[i];

                String filePath = file.getPath();
                if(filePath.endsWith(".pdf"))
                    fileList.add(file.getName());
            }
            return fileList.toArray(new String[fileList.size()]);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View layout = inflater.inflate(R.layout.popup, null);


            builder.setView(layout)

                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText fileName = (EditText) layout.findViewById(R.id.file);
                            try {

                                createPDF(fileName.getText().toString());
                                Toast.makeText(getContext(), "PDF Created Succesfully", Toast.LENGTH_SHORT).show();


                            } catch (Exception e) {
                                Log.wtf("qwerty", e.toString());
                                Toast.makeText(getContext(), "Create PDF error", Toast.LENGTH_SHORT).show();
                            }
                            try{
                                ListView FileView = (ListView) getActivity().findViewById(R.id.listActivity);
                                FileViewAdapter adapter = new FileViewAdapter(getActivity(), ReadPDFDirectory());
                                FileView.setAdapter(adapter);
                            }catch (Exception e){
                                Log.wtf("qwerty", e.toString());
                                Toast.makeText(getContext(), "List View Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FileCreate.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }

        public void createPDF(String text) {
            Document doc = new Document();
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath + "/PDFs" );
            if (!file.exists()) {
                file.mkdirs();
            }
            Date date = new Date() ;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
            String outpath = filepath + "/PDFs/" +  text + ".pdf";


            try {

                PdfWriter.getInstance(doc, new FileOutputStream(outpath));

                doc.open();
                doc.addCreationDate();
                doc.addTitle("Test App");
                doc.add(new Paragraph("Test App Test"));
                doc.close();

            } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DocumentException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mGoogleApiClient = ((MyApplication) getApplication()).getGoogleApiClient(ProfileActivity.this, this);
        CircleImageView profilePhoto = (CircleImageView) findViewById(R.id.profile_image);
        TextView profileUsername = (TextView) findViewById(R.id.profile_name);
        TextView profileEmail = (TextView) findViewById(R.id.profile_email);
        String profileDisplayName = returnValueFromBundles(MainActivity.PROFILE_DISPLAY_NAME);
        String profileUserEmail = returnValueFromBundles(MainActivity.PROFILE_USER_EMAIL);
        String profileImageLink = returnValueFromBundles(MainActivity.PROFILE_IMAGE_URL);
        Picasso.with(ProfileActivity.this).load(profileImageLink).into(profilePhoto);
        assert profileUsername != null;
        profileUsername.setText(profileDisplayName);
        assert profileEmail != null;
        profileEmail.setText(profileUserEmail);
        ListView FileView = (ListView) findViewById(R.id.listActivity);

        try{
            FileViewAdapter adapter = new FileViewAdapter(this, ReadPDFDirectory());
            FileView.setAdapter(adapter);
            FileView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                public void onItemClick(AdapterView<?>adapter,View v, int position, long x){

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/PDFs/" + adapter.getItemAtPosition(position).toString()));
                    intent.setType("application/pdf");
                    PackageManager pm = getPackageManager();
                    java.util.List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                    if (activities.size() > 0) {
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"OPENING PDF", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"ERROR NOT OPENING PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "No files found", Toast.LENGTH_LONG).show();
        }



    }
    private String[] ReadDates()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath + "/PDFs" );
        File[] files=f.listFiles();
        ArrayList<String> fileList = new ArrayList<>();
        for(int i=0; i<files.length; i++)
        {
            File file = files[i];
      /*It's assumed that all file in the path are in supported type*/
            String filePath = file.getPath();
            if(filePath.endsWith(".pdf")) // Condition to check .jpg file extension
                fileList.add(file.getName());
        }
        return fileList.toArray(new String[fileList.size()]);
    }
    private String[] ReadPDFDirectory()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath + "/PDFs" );
        File[] files=f.listFiles();
        ArrayList<String> fileList = new ArrayList<>();
        for(int i=0; i<files.length; i++)
        {
            File file = files[i];

            String filePath = file.getPath();
            if(filePath.endsWith(".pdf"))
                fileList.add(file.getName());
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create){
            android.app.FragmentManager fragmentManager = getFragmentManager();
            DialogFragment newFragment = new FileCreate();
            newFragment.show(fragmentManager, "pdf");
            ListView FileView = (ListView) findViewById(R.id.listActivity);
            FileView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                public void onItemClick(AdapterView<?>adapter,View v, int position, long x){

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/PDFs/" + adapter.getItemAtPosition(position).toString()));
                    intent.setType("application/pdf");
                    PackageManager pm = getPackageManager();
                    java.util.List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                    if (activities.size() > 0) {
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"OPENING PDF", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"ERROR NOT OPENING PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (id == R.id.action_logout) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                   new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent login = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(login);
                            finish();
                        }
                    });
            return true;
        }
        if (id == R.id.action_disconnect) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent login = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(login);
                            finish();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String returnValueFromBundles(String key) {
        Bundle inBundle = getIntent().getExtras();
        String returnedValue = inBundle.get(key).toString();
        return returnedValue;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}