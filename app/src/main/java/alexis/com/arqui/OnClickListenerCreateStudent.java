package alexis.com.arqui;

/**
 * Created by alexis on 08/05/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import alexis.com.easy_couchbase.CRUD;

public class OnClickListenerCreateStudent implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        final Context context = view.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.student_input_form, null, false);
        final EditText editTextStudentFirstname = (EditText) formElementsView.findViewById(R.id.editTextStudentFirstname);
        final EditText editTextStudentEmail = (EditText) formElementsView.findViewById(R.id.editTextStudentEmail);
        new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("Create Student")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String studentFirstname = editTextStudentFirstname.getText().toString();
                                String studentEmail = editTextStudentEmail.getText().toString();
                                Student s = new Student(studentFirstname,studentEmail);
                                Log.d("ALEXIS",s.toString());
                                String res = "";
                                try {
                                    res = create(s,context);
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                if(res!=null){
                                    Toast.makeText(context, "Student information was saved.", Toast.LENGTH_SHORT).show();
                                    try {
                                        ((MainActivity)context).readRecords();
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(context, "Unable to save student information.", Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
                            }
                        }).show();
    }

    public String create(Student objectStudent,Context context) throws InvocationTargetException, IllegalAccessException {
        CRUD crud = new CRUD(context);
        try {
            return crud.create(objectStudent,"Student");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}