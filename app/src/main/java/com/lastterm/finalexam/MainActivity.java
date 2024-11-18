package com.lastterm.finalexam;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lastterm.finalexam.bottomMenu.AdminHomeFragment;
import com.lastterm.finalexam.bottomMenu.OwnerHomeFragment;
import com.lastterm.finalexam.bottomMenu.TenantHomeFragment;
import com.lastterm.finalexam.bottomMenu.fragmentItem.FavoritesFragment;
import com.lastterm.finalexam.bottomMenu.fragmentItem.SettingFragment;
import com.lastterm.finalexam.room.RoomManagementFragment;
import com.lastterm.finalexam.bottomMenu.fragmentItem.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userRole;  // Variable to store user role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check the user's role before setting up the BottomNavigation
        checkUserRoleAndSetupNavigation();

        // Set default fragment if needed (can be changed based on role)
        if (savedInstanceState == null && userRole != null) {
            Fragment defaultFragment = getDefaultFragmentForRole(userRole);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, defaultFragment)
                    .commit();
        }
    }

    private void checkUserRoleAndSetupNavigation() {
        // Get the current user
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch user role from Firestore
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            // Get role from the Firestore document
                            userRole = document.getString("role");

                            // Setup BottomNavigation based on the user's role
                            setupBottomNavigation(userRole);

                            // Optionally, you can load a default fragment for the user role
                            loadDefaultFragment(userRole);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error if fetching role fails
                });
    }

    private void setupBottomNavigation(String role) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set the bottom navigation menu based on user role
        if ("tenant".equals(role)) {
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(R.menu.bottom_navigation_tenant);  // Menu for tenant
        } else if ("owner".equals(role)) {
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(R.menu.bottom_navigation_owner);  // Menu for owner
        } else if ("admin".equals(role)) {
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(R.menu.bottom_navigation_admin);  // Menu for admin
        }

        // Set listener for navigation item selection
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Check selected role and item
            if ("tenant".equals(userRole)) {
                // Fragment for Tenant
                if (item.getItemId() == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (item.getItemId() == R.id.nav_favorites) {
                    selectedFragment = new FavoritesFragment();
                } else if (item.getItemId() == R.id.nav_setting) {
                    selectedFragment = new SettingFragment();
                }
            } else if ("owner".equals(userRole)) {
                // Fragment cho Owner
                if (item.getItemId() == R.id.nav_room_management) {
                    selectedFragment = new RoomManagementFragment();
                }
                else if (item.getItemId() == R.id.nav_setting) {
                    selectedFragment = new SettingFragment();
                }

            } else if ("admin".equals(userRole)) {
                // Fragment cho Admin

            }

            // Replace Fragment
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

    }

    private void loadDefaultFragment(String role) {
        Fragment defaultFragment = getDefaultFragmentForRole(role);
        if (defaultFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, defaultFragment)
                    .commit();
        }
    }

    private Fragment getDefaultFragmentForRole(String role) {
        if ("tenant".equals(role)) {
            return new TenantHomeFragment();
        } else if ("owner".equals(role)) {
            return new OwnerHomeFragment();
        } else if ("admin".equals(role)) {
            return new AdminHomeFragment();
        }
        return null;
    }
}