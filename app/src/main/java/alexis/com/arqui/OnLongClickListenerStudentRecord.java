package alexis.com.arqui;

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

import alexis.com.easy_couchbase.CRUD;

/**
 * Created by alexis on 08/05/17.
 */

public class OnLongClickListenerStudentRecord implements View.OnLongClickListener {

    Context appContext;
    Context context;
    String id;

    public OnLongClickListenerStudentRecord(Context appContext){
        this.appContext = appContext;
    }

    @Override
    public boolean onLongClick(View view) {

        context = view.getContext();
        id = view.getTag().toString();

        final CharSequence[] items = { "Edit", "Delete" };

        new AlertDialog.Builder(context).setTitle("Student Record")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        if (item == 0) {
                            editRecord(id);
                        }else{

                            CRUD crud = new CRUD(appContext);
                            boolean deleteSuccessful = false;
                            try {
                                deleteSuccessful = crud.delete(id);
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }

                            if (deleteSuccessful){
                                Toast.makeText(context, "Student record was deleted.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Unable to delete student record.", Toast.LENGTH_SHORT).show();
                            }

                            try {
                                ((MainActivity) context).readRecords();
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }


                        }

                        dialog.dismiss();

                    }
                }).show();
        return false;
    }

    public void editRecord(final String idStudent){
        final CRUD crud = new CRUD(appContext);
        Log.d("ALEXIS",idStudent);
        Student student = (Student) crud.find(idStudent,"Student",Student.class);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.student_input_form, null, false);
        final EditText editTextStudentFirstname = (EditText) formElementsView.findViewById(R.id.editTextStudentFirstname);
        final EditText editTextStudentEmail = (EditText) formElementsView.findViewById(R.id.editTextStudentEmail);
        editTextStudentFirstname.setText(student.getName());
        editTextStudentEmail.setText(student.getEmail());
        new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("Edit Record")
                .setPositiveButton("Save Changes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Student objectStudent = new Student(
                                        editTextStudentFirstname.getText().toString(),
                                        editTextStudentEmail.getText().toString());
                                objectStudent.setId(idStudent);

                                boolean updateSuccessful = false;
                                try {
                                    updateSuccessful = crud.update(idStudent,
                                            "Student",objectStudent);
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }

                                if(updateSuccessful){
                                    Toast.makeText(context, "Student record was updated.", Toast.LENGTH_SHORT).show();
                                    try {
                                        ((MainActivity) context).readRecords();
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(context, "Unable to update student record.", Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
                            }

                        }).show();
    }




}