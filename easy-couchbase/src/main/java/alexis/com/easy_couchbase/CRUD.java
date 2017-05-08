package alexis.com.easy_couchbase;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by alexis on 08/05/17.
 */

public class CRUD {

    private final String TAG = "CouchbaseEvents";
    DatabaseManager dbm;

    /**
     * Constructor
     * @param applicationContext Contexto de la aplicacion que usa la libreria
     */
    public CRUD(Context applicationContext){
        dbm = DatabaseManager.getInstance(applicationContext);
    }

    /**
     * Guarda un objecto en la base de datos
     * @param o Objeto
     * @param type Coleccion o tipo bajo el que que guardara el objeto
     * @return el id del documento en el que se guardo, regresa null si no se guardo
     */
    public String create(Object o,String type) throws InvocationTargetException, IllegalAccessException {

        Class[] args = new Class[0];
        Method method = null;
        try {
            method = o.getClass().getDeclaredMethod("getId",args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object id = method.invoke(o);

        if(id!=null){
            type = type.toLowerCase();

            Document document = this.dbm.getDatabase().getDocument(id.toString());

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("type",type);
            properties.put(type,o);

            try {
                document.putProperties(properties);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

            Log.d(TAG,document.getProperties().toString());
            return document.getId();

        }else{
            return null;
        }
    }

    /**
     * Lista los objetos persistidos de un cierto tipo
     * @param type String con el tipo de documento que se requiere
     * @return Una lsita de objetos del tipo dado
     * @throws ClassNotFoundException
     * @throws CouchbaseLiteException
     */
    public List<Object> list(String type,Class classO) throws ClassNotFoundException, CouchbaseLiteException, IOException {
        Query query = this.dbm.getDatabase().createAllDocumentsQuery();
        type = type.toLowerCase();
        String className = Character.toUpperCase(type.charAt(0)) + type.substring(1);
        Class c = classO;
        List<Object> list = new ArrayList<Object>();
        LiveQuery lq = query.toLiveQuery();
        QueryEnumerator queryEnumerator = lq.run();
        Gson gson = new Gson();
        for(Iterator<QueryRow> it=queryEnumerator;it.hasNext();){
            QueryRow row = it.next();
            Document rowDocument = row.getDocument();
            if(rowDocument.getProperties().get("type").equals(type)){


                String jsonString = gson.toJson(rowDocument.getProperties().get(type), Map.class);
                Object o = gson.fromJson(jsonString, c);
                list.add(o);

            }
        }
        return list;
    }

    /**
     * Recupera un objeto en la base de datos si esta en algun documento
     * @param id String id del documento a buscar
     * @param type El nombre del parametro que se buscara en el documento, siendo igual que el nombre que se le pase o de la clase
     * @param objectClass Clase del objeto que se quiere recuperar
     * @return Object con el objeto requerido si es encontrado, null si no esta
     */
    public Object find(String id,String type,Class objectClass){
        Document document = this.dbm.getDatabase().getExistingDocument(id);
        if(document!=null){
            Gson gson = new Gson();
            String jsonString = gson.toJson(
                    document.getProperties().get(type.toLowerCase()), Map.class);
            Object o = gson.fromJson(jsonString, objectClass);
            return o;
        }else{
            return null;
        }
    }

    /**
     * Actualiza un documento
     * @param id String id del documento a buscar
     * @param type Tipo del objeto que se buscara
     * @param o Objeto que se quiere actualizar
     * @return true si fue posible actualizar,falso en caso contrario
     * @throws CouchbaseLiteException
     */
    public boolean update(String id,String type,Object o) throws CouchbaseLiteException {
        Document document = this.dbm.getDatabase().getExistingDocument(id);
        if(document!=null){
            ObjectMapper objectMapper = new ObjectMapper();
            final Map<String,Object> mapaObjeto = objectMapper.convertValue(o,Map.class);

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.putAll(document.getProperties());
            properties.put(type.toLowerCase(),mapaObjeto);
            document.putProperties(properties);
            return true;
        }else{
            Log.d("Llega","Se queda nulo");
            return false;
        }
    }

    /**
     * Borra un documento si el id existe
     * @param id id del documento que se quiere borrar
     * @return
     */
    public boolean delete(String id) throws CouchbaseLiteException {
        Document document = this.dbm.getDatabase().getExistingDocument(id);
        if(document!=null){
            return document.delete();
        }else{
            return false;
        }
    }

    /**
     * Genera una nueva base de datos con el mismo nombre vaciando la anterior
     * @throws CouchbaseLiteException
     */
    public void cleanDatabase() throws CouchbaseLiteException {
        this.dbm.cleanDatabase();
    }

}
