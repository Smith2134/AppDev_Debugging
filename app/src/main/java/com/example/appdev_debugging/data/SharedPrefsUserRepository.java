package com.example.appdev_debugging.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.appdev_debugging.data.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SharedPrefsUserRepository implements UserRepository {

    private static final String PREFS_NAME = "user_repository_prefs";
    private final SharedPreferences prefs;
    private final Map<String, User> usersByEmail = new HashMap<>();

    public SharedPrefsUserRepository(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadUsersFromPrefs();
    }

    private void loadUsersFromPrefs() {
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof String) {
                User user = deserializeUser((String) entry.getValue());
                if (user != null) {
                    String norm = normalize(user.getEmail());
                    if ("paulccampos@gmail.com".equalsIgnoreCase(norm)) {
                        prefs.edit().remove(entry.getKey()).apply();
                    } else {
                        usersByEmail.put(norm, user);
                    }
                }
            }
        }
    }

    @Override
    public synchronized User findByEmail(String email) {
        if (email == null) return null;
        return usersByEmail.get(normalize(email));
    }

    @Override
    public synchronized boolean exists(String email) {
        return email != null && usersByEmail.containsKey(normalize(email));
    }

    @Override
    public synchronized boolean add(User user) {
        if (user == null || user.getEmail() == null) {
            return false;
        }
        String normalized = normalize(user.getEmail());
        if (usersByEmail.containsKey(normalized)) {
            return false;
        }
        usersByEmail.put(normalized, user);
        String json = serializeUser(user);
        if (json != null) {
            prefs.edit().putString("user_" + normalized, json).apply();
        }
        return true;
    }

    @Override
    public synchronized boolean remove(String email) {
        if (email == null) return false;
        String normalized = normalize(email);
        boolean existed = usersByEmail.remove(normalized) != null;
        prefs.edit().remove("user_" + normalized).apply();
        return existed;
    }

    @Override
    public synchronized void clearAll() {
        usersByEmail.clear();
        prefs.edit().clear().apply();
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String serializeUser(User user) {
        try {
            JSONObject json = new JSONObject();
            json.put("fullName", user.getFullName());
            json.put("email", user.getEmail());
            json.put("passwordHash", user.getPasswordHash());
            json.put("salt", user.getSalt());
            return json.toString();
        } catch (JSONException e) {
            return null;
        }
    }

    private static User deserializeUser(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            return new User(
                    json.getString("fullName"),
                    json.getString("email"),
                    json.getString("passwordHash"),
                    json.getString("salt")
            );
        } catch (JSONException e) {
            return null;
        }
    }
}
