package com.example.perfin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;

    private FirebaseAuth auth;
    private GoogleSignInClient googleClient;

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // Auto-login
        if (auth.getCurrentUser() != null) {
            openMain();
            return;
        }

        // Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoogle = findViewById(R.id.btnGoogle);
        TextView tvGuest = findViewById(R.id.tvGuest);
        TextView tvSignUp = findViewById(R.id.tvSignUp);

        // Google Sign-In config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this, gso);

        // Click listeners
        btnLogin.setOnClickListener(v -> loginWithEmail());
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
        tvGuest.setOnClickListener(v -> signInAsGuest());
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }

    // ---------------- EMAIL LOGIN ----------------

    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> openMain())
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ---------------- GOOGLE LOGIN ----------------

    private void signInWithGoogle() {
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                                .getResult(ApiException.class);

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {

                    String uid = auth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", acct.getDisplayName());
                    user.put("email", acct.getEmail());
                    user.put("provider", "google");
                    user.put("monthlyBudget", 0.0);
                    user.put("monthlySavings", 0.0);
                    user.put("createdAt", System.currentTimeMillis());

                    // 🔥 Save / update user in Firestore
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener(unused -> openMain())
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            this,
                                            "Failed to save user: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ---------------- GUEST LOGIN ----------------

    private void signInAsGuest() {
        auth.signInAnonymously()
                .addOnSuccessListener(result -> openMain())
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ---------------- NAVIGATION ----------------

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
