package com.example.perfin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class FirebaseUtil {

    // Callback interface
    public interface AuthCallback {
        void onAuthReady();
    }

    // Proper method with callback parameter
    public static void ensureAuth(AuthCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            callback.onAuthReady();
        } else {
            auth.signInAnonymously()
                    .addOnSuccessListener(result -> callback.onAuthReady())
                    .addOnFailureListener(e -> {
                        // optional: log error
                        e.printStackTrace();
                    });
        }
    }
}
