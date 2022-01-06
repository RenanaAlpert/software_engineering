package com.example.tecknet.view.update_profile_technician;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tecknet.controller.shared_controller;
import com.example.tecknet.controller.technician_controller;
import com.example.tecknet.databinding.FragmentUpdateProfileTechnicianBinding;
import com.example.tecknet.model.UserInt;
import com.example.tecknet.view.UserViewModel;

public class UpdateProfileTechnicianFragment extends Fragment {

    private UpdateProfileTechnicianViewModel updateProfileTechnicianViewModel;
    private UserViewModel uViewModel;
    private FragmentUpdateProfileTechnicianBinding binding;

    private EditText fName , lName, changePass , changePass2 , currPass;
    private Spinner area;
    private Button updateBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        updateProfileTechnicianViewModel =
                new ViewModelProvider(this).get(UpdateProfileTechnicianViewModel.class);

        binding = FragmentUpdateProfileTechnicianBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        uViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserInt user=uViewModel.getItem().getValue();

        fName = binding.changeFName;
        lName = binding.changeLName;
        changePass = binding.newPass;
        changePass2 = binding.newPassConfirm;
        currPass = binding.currPass;
        area = binding.changeTechArea;
        updateBtn = binding.button ;
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check_valid_chang_pass(user , changePass.getText().toString(),changePass2.getText().toString()
                        , currPass.getText().toString())) {
                    //controller.update_user_details
                    shared_controller.update_user_details(user.getPhone(), fName.getText().toString(), lName.getText().toString());

                    //controller.update_area
                    technician_controller.update_technician_area(user.getPhone(), area);
                    clear_edit_text();
                    Toast.makeText(getActivity() , "עדכון פרופיל הצליח" , Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private boolean check_valid_chang_pass(UserInt user , String pass1 , String pass2 ,String curr) {
        boolean temp = false;

        if (!pass1.equals("") && !pass1.equals(" ") && !pass1.equals("\n")) {
            temp = true;
        }
        if(!pass2.equals("") && !pass2.equals(" ") && !pass2.equals("\n")) {
            temp = true;
        }
        if(!curr.equals("") && !curr.equals(" ") && !curr.equals("\n")) {
            temp = true;
        }
        if(temp){
            if (user.getPass().equals(curr)) {
                if (pass1.equals(pass2)) {
                    //call the controller
                    shared_controller.update_user_pass(user.getPhone(), changePass);
                    shared_controller.change_user_pass(user.getEmail(), pass1);
                    user.setPass(pass1);
                    return  true;
                }
                else {
                    changePass.setError("הסיסמאות לא תואמים");
                    changePass2.setError("הסיסמאות לא תואמים");
                    return  false;
                }
            }
            else {
                currPass.setError("סיסמה שגויה");
                return  false;
            }
        }
        return true;
    }
    private void clear_edit_text(){
        fName.getText().clear();
        lName.getText().clear();
        changePass.getText().clear();
        changePass2.getText().clear();
        currPass.getText().clear();

    }
}