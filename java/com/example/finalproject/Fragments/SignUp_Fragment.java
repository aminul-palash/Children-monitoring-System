package com.example.finalproject.Fragments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.finalproject.Activities.MainActivity;
import com.example.finalproject.Activities.TypeSelect;
import com.example.finalproject.Custom.CustomToast;
import com.example.finalproject.R;
import com.example.finalproject.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignUp_Fragment extends Fragment implements OnClickListener {
	private static View view;
	private static EditText fullName, emailId, mobileNumber, location,
			password, confirmPassword;
	private static TextView login;
	private static ProgressBar progressBar;
	private static Button signUpButton;
	private static CheckBox terms_conditions;
	FirebaseAuth mAuth;

	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.signup_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize all views
	private void initViews() {
		//fullName = (EditText) view.findViewById(R.id.fullName);
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		///mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		//location = (EditText) view.findViewById(R.id.location);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		progressBar = view.findViewById(R.id.progressbar);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
		mAuth = FirebaseAuth.getInstance();

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			login.setTextColor(csl);
			terms_conditions.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signUpBtn:
			progressBar.setVisibility(View.VISIBLE);
			// Call checkValidation method
			checkValidation();
			break;

		case R.id.already_user:

			// Replace login fragment
			new MainActivity().replaceLoginFragment();
			break;
		}

	}

	// Check Validation Method
	private void checkValidation() {

		// Get all edittext texts
		///String getFullName = fullName.getText().toString();
		String getEmailId = emailId.getText().toString();
		///String getMobileNumber = mobileNumber.getText().toString();
		//String getLocation = location.getText().toString();
		String getPassword = password.getText().toString();
		String getConfirmPassword = confirmPassword.getText().toString();

		// Pattern match for email id
		Pattern p = Pattern.compile(Utils.regEx);
		Matcher m = p.matcher(getEmailId);

		// Check if all strings are null or not
		if (getEmailId.equals("") || getEmailId.length() == 0||
				getPassword.equals("") || getPassword.length() == 0
				|| getConfirmPassword.equals("")
				|| getConfirmPassword.length() == 0){

			new CustomToast().Show_Toast(getActivity(), view,
					"All fields are required.");
		progressBar.setVisibility(View.GONE);}

		// Check if email id valid or not
		else if (!m.find()){
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
			progressBar.setVisibility(View.GONE);}

		// Check if both password should be equal
		else if (!getConfirmPassword.equals(getPassword)){
			new CustomToast().Show_Toast(getActivity(), view,
					"Both password doesn't match.");
			progressBar.setVisibility(View.GONE);}

		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked()){
			new CustomToast().Show_Toast(getActivity(), view,
					"Please select Terms and Conditions.");
			progressBar.setVisibility(View.GONE);}

		// Else do signup or do your stuff
		else{
			//Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT)
					///.show();
			mAuth.createUserWithEmailAndPassword(getEmailId, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					progressBar.setVisibility(View.GONE);
					if (task.isSuccessful()) {
						getActivity().finish();
						startActivity(new Intent(getActivity(), TypeSelect.class));
					} else {

						if (task.getException() instanceof FirebaseAuthUserCollisionException) {
							///Toast.makeText(getActivity(), "You are already registered", Toast.LENGTH_SHORT).show();
							new CustomToast().Show_Toast(getActivity(), view,
									"You are already registered");
							///progressBar.setVisibility(View.GONE);

						} else {
							///Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
							new CustomToast().Show_Toast(getActivity(), view,
									task.getException().getMessage());
							///progressBar.setVisibility(View.GONE);
						}

					}
				}
			});
		}


	}
}
