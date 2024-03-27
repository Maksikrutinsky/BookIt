package com.example.newbookit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class ForgetPassActivityTest {


    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private Task<Void> task;


    @Test
    public void sendPasswordResetEmail_Success() {
        doAnswer(invocation -> {
            OnCompleteListener<Void> callback = invocation.getArgument(0);
            callback.onComplete(task);
            return null;
        }).when(task).addOnCompleteListener(any());

        verify(firebaseAuth).sendPasswordResetEmail(anyString());

    }

    @Test
    public void sendPasswordResetEmail_Failure() {
        doAnswer(invocation -> {
            OnCompleteListener<Void> callback = invocation.getArgument(0);
            when(task.isSuccessful()).thenReturn(false); // הגדרת המשימה כלא מוצלחת
            callback.onComplete(task);
            return null;
        }).when(firebaseAuth).sendPasswordResetEmail(anyString());

        firebaseAuth.sendPasswordResetEmail("test@example.com");

        verify(task).addOnCompleteListener(any(OnCompleteListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendPasswordResetEmail_InvalidEmail() {
        firebaseAuth.sendPasswordResetEmail("invalidEmail"); // הזנת דוא"ל לא חוקי

        verify(firebaseAuth, never()).sendPasswordResetEmail(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendPasswordResetEmail_EmptyEmail() {
        firebaseAuth.sendPasswordResetEmail(""); // שליחת דוא"ל ריק

        verify(firebaseAuth, never()).sendPasswordResetEmail(anyString());
    }

}