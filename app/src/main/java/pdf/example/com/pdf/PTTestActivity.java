package pdf.example.com.pdf;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFNet;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.sdf.SDFDoc;

import java.io.IOException;
import java.io.InputStream;

public class PTTestActivity extends Activity {

    public PDFViewCtrl mPDFViewCtrl;
    public String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the library
        try {
            PDFNet.initialize(this, R.raw.pdfnet);
        } catch (PDFNetException e) {
            // Do something...
            e.printStackTrace();
        }

        // Inflate the view and get a reference to PDFViewCtrl
        setContentView(R.layout.main);
        mPDFViewCtrl = (PDFViewCtrl) findViewById(R.id.pdfviewctrl);

        mPDFViewCtrl.setToolManager(new ToolManager(mPDFViewCtrl));

        // Load a document
        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.sample);
        try {
            filePath = getIntent().getExtras().getString("file");
            PDFDoc doc = new PDFDoc(filePath);

            //PDFDoc doc = new PDFDoc(is);
            mPDFViewCtrl.setDoc(doc);

            // Or you can use the full path instead
            //doc = new PDFDoc("/mnt/sdcard/sample.pdf");
        } catch (PDFNetException e) {
            e.printStackTrace();
        }
        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPDFViewCtrl.getDoc().save(filePath, SDFDoc.e_incremental, null);
                    Toast.makeText(getApplicationContext(), "PDF Saved!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error while saving PDF", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        // This method simply stops the current ongoing rendering thread, text
        // search thread, and tool
        super.onPause();
        if (mPDFViewCtrl != null) {
            mPDFViewCtrl.pause();
        }
    }

    @Override
    protected void onResume() {
        // This method simply starts the rendering thread to ensure the PDF
        // content is available for viewing.
        super.onResume();
        if (mPDFViewCtrl != null) {
            mPDFViewCtrl.resume();
        }
    }

    @Override
    protected void onDestroy() {
        // Destroy PDFViewCtrl and clean up memory and used resources.
        try {
            mPDFViewCtrl.getDoc().save(filePath, SDFDoc.e_linearized, null);
            Toast.makeText(getApplicationContext(), "PDF Saved!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error while saving PDF", Toast.LENGTH_LONG).show();
        }
        super.onDestroy();
        if (mPDFViewCtrl != null) {
            mPDFViewCtrl.destroy();
        }
    }

    @Override
    public void onLowMemory() {
        // Call this method to lower PDFViewCtrl's memory consumption.
        super.onLowMemory();
        if (mPDFViewCtrl != null) {
            mPDFViewCtrl.purgeMemory();
        }
    }
}