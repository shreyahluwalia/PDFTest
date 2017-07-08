package pdf.example.com.pdf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Shrey on 7/7/17.
 */

public class FileViewAdapter extends BaseAdapter {
    Context context;
    String[] fileNames;
    String[] fileDates;
    private static LayoutInflater inflater = null;

    public FileViewAdapter(Context context, String[] fileNames) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.fileNames = fileNames;
        this.fileDates = null;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return fileNames.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return fileNames[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        TextView fileName = (TextView) vi.findViewById(R.id.fileName);

        fileName.setText(fileNames[position]);
        //fileDate.setText(fileDates[position]);
        return vi;
    }
    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.setDataAndType(Uri.fromFile(fileL), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        //startActivity(intent);
    }
}
