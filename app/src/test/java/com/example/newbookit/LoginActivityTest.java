package com.example.newbookit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.junit.Test;

public class LoginActivityTest {

    @Test
    public void testCheckUser_User() {

        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.child("userType").getValue(String.class)).thenReturn("user");

        String userType = checkUserType(snapshot);

        assertEquals("user", userType);
    }

    @Test
    public void testCheckUserType_UserTypeExists_ReturnsUserType() {

        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.child("userType").getValue(String.class)).thenReturn("user");

        String userType = checkUserType(snapshot);

        assertEquals("user", userType);
    }

    @Test
    public void testCheckUserType_UserTypeDoesNotExist_ReturnsEmptyString() {
        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.child("userType").getValue(String.class)).thenReturn(null);

        String userType = checkUserType(snapshot);

        assertEquals("", userType);
    }

    @Test
    public void testCheckUserType_UserTypeIsEmpty_ReturnsEmptyString() {
        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.child("userType").getValue(String.class)).thenReturn("");

        String userType = checkUserType(snapshot);

        assertEquals("", userType);
    }

    public String checkUserType(DataSnapshot snapshot) {

        String userType = snapshot.child("userType").getValue(String.class);

        return userType != null ? userType : "";
    }

    @Test
    public void validateData_InvalidEmail_ShowToast() {
        LoginActivity activity = new LoginActivity();
        activity.passLogin.setText("");
        activity.userLogin.setText("Noam");

        assertFalse(activity.validateData());
    }

    @Test
    public void validateData_LongPassword_ReturnsTrue() {
        LoginActivity activity = new LoginActivity();
        activity.passLogin.setText("thisisaverylongpasswordthatshouldbevalid");

        assertFalse(activity.validateData());
    }

    @Test
    public void validateData_InvalidEmailFormat_ShowToast() {
        LoginActivity activity = new LoginActivity();
        activity.passLogin.setText("password123");
        activity.userLogin.setText("invalidemail");

        assertFalse(activity.validateData());
    }

    @Test
    public void validateData_EmptyPassword_ShowToast() {
        LoginActivity activity = new LoginActivity();
        activity.passLogin.setText("");
        activity.userLogin.setText("example@example.com");

        assertFalse(activity.validateData());
    }



}