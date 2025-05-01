package com.example.scheduleapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.scheduleapp.R;
import com.example.scheduleapp.utils.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private PreferencesManager preferencesManager;
    private TextInputEditText editTextName;
    private TextInputEditText editTextEmail;
    private SwitchMaterial switchTheme;
    private MaterialButton buttonSave;
    private ShapeableImageView imageViewProfile;
    private FloatingActionButton fabChangePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        preferencesManager = new PreferencesManager(requireContext());
        
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        switchTheme = view.findViewById(R.id.switchTheme);
        buttonSave = view.findViewById(R.id.buttonSave);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        fabChangePhoto = view.findViewById(R.id.fabChangePhoto);

        // Load saved preferences
        editTextName.setText(preferencesManager.getUserName());
        editTextEmail.setText(preferencesManager.getUserEmail());
        switchTheme.setChecked(preferencesManager.isDarkMode());

        // Load profile photo if exists
        String profilePhotoUri = preferencesManager.getProfilePhotoUri();
        if (profilePhotoUri != null && !profilePhotoUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(profilePhotoUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                imageViewProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set up listeners
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        fabChangePhoto.setOnClickListener(v -> showImagePickerDialog());

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();

            preferencesManager.setUserName(name);
            preferencesManager.setUserEmail(email);

            Snackbar.make(view, "Profile updated successfully", Snackbar.LENGTH_SHORT).show();
        });

        return view;
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Profile Photo")
            .setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Take photo
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    // Choose from gallery
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                }
            })
            .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                        imageViewProfile.setImageBitmap(bitmap);
                        preferencesManager.setProfilePhotoUri(selectedImage.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageViewProfile.setImageBitmap(imageBitmap);
                    // Save the bitmap to storage and get URI
                    String uri = MediaStore.Images.Media.insertImage(
                        requireContext().getContentResolver(),
                        imageBitmap,
                        "profile_photo",
                        "Profile photo"
                    );
                    if (uri != null) {
                        preferencesManager.setProfilePhotoUri(uri);
                    }
                }
            }
        }
    }
} 