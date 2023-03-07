package com.example.tasteit_java.bdConnection;

import static org.neo4j.driver.Values.parameters;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.tasteit_java.clases.User;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BdConnection implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(BdConnection.class.getName());
    private final Driver driver;

    public BdConnection(String uri, String user, String password) {
        // The driver is a long living object and should be opened during the start of your application
        //driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        // The driver object should be closed before the application ends.
        driver.close();
    }

    public User login(final String token) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        User user = null;
        try {
            //Esto es para que no salte excepcion de Sincronia (pdte hacer algo)
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = driver.session();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (n:User {token:\"" + token + "\"}) RETURN n.username, n.biography, n.imgProfile;");
            Result result = session.run(query);
            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next(); //Guardamos el registro
                String name = record.get(0).asString();
                String biography = record.get(1).asString();
                String imgProfile = record.get(2).asString();
                user = new User(name, biography, imgProfile); //Lo mostramos segun su tipo
            }
            session.close(); //Cerramos la sesión
            return user; //Retornamos el valor
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }

    }

    public void register(final String username, final String token) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        try {
            //Esto es para que no salte excepcion de Sincronia (pdte hacer algo)
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //Iniciamos una sesion con la bd
            Session session = driver.session();
            //Creamos la sentencia que se ejecutara para crear el usuario
            session.writeTransaction(tx -> {
                Query query = new Query("CREATE (" + username + ":User {username: $username, token: $token})", parameters("username", username, "token", token));
                tx.run(query);
                return null;
            });

            session.close(); //Cerramos la sesión
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }
    }

    /*
    MATCH (n:Users) WHERE n.username = '" + username + "' AND n.password = '" + password + "' RETURN CASE WHEN n IS NOT NULL THEN true ELSE false END AS n;
    MATCH (n:Users) WHERE n.username = $username AND n.password = $password RETURN CASE WHEN n IS NOT NULL THEN true ELSE false END AS n; //Devolver BOOLEAN si se encuentra o no
    MATCH (n:Users) WHERE n.username = $username AND n.password = $password RETURN
    CREATE (user3:Users {name:'user3', username:'user3', password:'user3', followers:[], following:[], recipes:[], biography:'', photos:[], videos:[], perfilimg:''}); //Sentencia para crear users
    */
}


