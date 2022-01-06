package com.example.tecknet.view.my_malfunctions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import androidx.media.app.NotificationCompat;

import com.example.tecknet.R;
import com.example.tecknet.controller.technician_controller;
import com.example.tecknet.model.InstitutionDetailsInt;
import com.example.tecknet.model.MalfunctionDetailsInt;
import com.example.tecknet.model.ProductDetailsInt;
import com.example.tecknet.model.UserInt;
import com.example.tecknet.model.malfunctionView;

import java.util.ArrayList;

public class MyMalfunctionsAdapter extends ArrayAdapter<malfunctionView> {

    private final Context mContext;
    private int mResource;
    private ArrayList<malfunctionView> arrMals;
    private MalfunctionDetailsInt mal;
    private ProductDetailsInt product;
    private InstitutionDetailsInt ins;


    public MyMalfunctionsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<malfunctionView> obj) {
        super(context, resource, obj);
        mContext = context;
        mResource = resource;
        arrMals = obj;

    }

//    static class ViewHolder {
//        TextView text;
//        Button btn;
//    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater linf = LayoutInflater.from(mContext);
        convertView = linf.inflate(R.layout.fragment_my_malfunctions_row, parent, false);
        TextView tvName = convertView.findViewById(R.id.School);
        TextView tvAddress = convertView.findViewById(R.id.Address);
        TextView tvDevice = convertView.findViewById(R.id.BreakDevice);
        TextView tvType = convertView.findViewById(R.id.type);
        TextView tvExplain = convertView.findViewById(R.id.FaultDescription);
        TextView tvContact = convertView.findViewById(R.id.Contact);
        TextView tvStatus = convertView.findViewById(R.id.Status);

        mal = getItem(position).getMal();
        product = getItem(position).getProduct();
        ins = getItem(position).getIns();
        UserInt maintenance = getItem(position).getUser();

        tvName.setText(ins.getName());
        tvAddress.setText(ins.getAddress() + " " + ins.getCity());
        tvDevice.setText(product.getDevice());
        tvType.setText(product.getType());
        tvExplain.setText(mal.getExplanation());
        tvContact.setText(maintenance.getFirstName() + " " + maintenance.getLastName() + " - " + ins.getContact());
        tvStatus.setText(mal.getStatus());

        Button button = convertView.findViewById(R.id.button);
        if (mal.getStatus().equals("נלקח לטיפול")) {
            button.setText("התחל טיפול");
        }
        else if (mal.getStatus().equals("בטיפול")) {
            button.setText("לסיום");
        }
        else if (mal.getStatus().equals("מחכה לתשלום") || mal.getStatus().equals("שולם")){
            button.setEnabled(false);
        }

            button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().equals("התחל טיפול")) {
                    technician_controller.set_status_malfunction(mal.getMal_id(), "בטיפול");
                    button.setText("לסיום");
                    arrMals.clear(); /// yuval added this line to refresh the list view
                }

                else if (button.getText().equals("לסיום")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                    final EditText edittext = new EditText(v.getContext());
                    alert.setMessage("\nעלות הטיפול:");
                    alert.setTitle("מחיר");

                    alert.setView(edittext);

                    alert.setPositiveButton("שלח", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (false){
                                Toast.makeText(mContext, "לא הוכנס מחיר", Toast.LENGTH_LONG).show();
                            }
                            else {
                                String pay = edittext.getText().toString();
                                double payment = Double.parseDouble(pay);
                                technician_controller.set_payment_nalfunction(mal.getMal_id(), payment);
//                            if(!checkPermission(Manifest.permission.SEND_SMS)){
//                                ActivityCompat.requestPermissions((Activity) mContext,new String[]{Manifest.permission.SEND_SMS},1);
////                                ActivityCompat.requestPermissions();
//                            }
                                sendSMS(payment);

                                technician_controller.set_status_malfunction(mal.getMal_id(), "מחכה לתשלום");
                            }
                        }
                    });

                    alert.setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            //arrMals.clear(); /// yuval added this line to refresh the list view
                        }
                    });

                    alert.show();
                }
            }
        });

        return convertView;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void sendSMS(double pay){
        String title = "התקלה טופלה בהצלחה!";
        String text = "התקלה במכשיר " + product.getType() + " - " + mal.getExplanation()
                + " תוקנה בהצלחה. מחיר הטיפול הינו " + pay + " ש\"ח";

        // Create the intent.
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//        // Set the data for the intent as the phone number.
//        smsIntent.setData(Uri.parse(ins.getContact()));
//        // Add the message (sms) with the key ("sms_body").
//        smsIntent.putExtra("sms_body", text);
//        // If package resolves (target app installed), send intent.
//        if (smsIntent.resolveActivity(mContext.getPackageManager()) != null) {
//            mContext.startActivity(smsIntent);
//            Toast.makeText(mContext, "ההודעה נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
//        } else {
//            Log.d(TAG, "Can't resolve app for ACTION_SENDTO Intent");
//            Toast.makeText(mContext, "משהו נכשל. אנא נסה שוב", Toast.LENGTH_SHORT).show();
//        }
        try{
            SmsManager smsManager = SmsManager.getDefault();
            ActivityCompat.requestPermissions((Activity) mContext,new String[]{Manifest.permission.SEND_SMS},1);
            smsManager.sendTextMessage(ins.getContact(), null, text, null, null);
            Toast.makeText(mContext, "ההודעה נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, "משהו נכשל. אנא נסה שוב", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(mContext, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}