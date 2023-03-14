package com.example.tasteit_java.bdConnection;

import static org.neo4j.driver.Values.parameters;

import android.os.StrictMode;

import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.Recipe;
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
import org.neo4j.driver.types.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BdConnection implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(BdConnection.class.getName());
    private static Driver driver;

    //NEO4J
    private final String uri = "neo4j+s://dc95b24b.databases.neo4j.io"; //URL conexion Neo4j
    private final String user = "neo4j";
    private final String pass = "sBQ6Fj2oXaFltjizpmTDhyEO9GDiqGM1rG-zelf17kg"; //PDTE CIFRAR
    //FIN NEO

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    public BdConnection() {
        // The driver is a long living object and should be opened during the start of your application
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pass));
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void close() {
        // The driver object should be closed before the application ends.
        driver.close();
    }

    public static Session openSession() {
        return driver.session();
    }

    public static void closeSession(Session session) {
        session.close();
    }

    /*public User login(final String token) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        User user = null;
        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
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
            closeSession(session); //Cerramos la sesión
            return user; //Retornamos el valor
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }

    }*/

    public void register(final String username, final String token) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();

            //Creamos la sentencia que se ejecutara para crear el usuario
            session.writeTransaction(tx -> {
                Query query = new Query("CREATE (" + username + ":User {username: $username, token: $token})", parameters("username", username, "token", token));
                tx.run(query);
                return null;
            });

            closeSession(session); //Cerramos la sesión
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }
    }

    public void createRecipe(Recipe recipe, String token){

        Recipe r = recipe;
        //creacion receta
        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();
            //fecha
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateCreated = sdf.format(c.getTime());

            //Creamos la sentencia que se ejecutara para crear la receta ademas se crea la  relacion entre el creador y la receta
            /*
            session.writeTransaction(tx -> {
                Query query = new Query("CREATE (" + r.getName().replaceAll(" ", "") + ":Recipe {name: $name, description: $description, steps: $steps, image: $image, dateCreated: date($dateCreated), country: $country})",
                        parameters("name", r.getName(), "description", r.getDescription(),"steps",r.getSteps(),"image",r.getImage(),"dateCreated", dateCreated,"country",r.getCountry()));
                tx.run(query);
                return null;
            });
            */
            session.writeTransaction(tx -> {
                        Query query = new Query("MATCH (a:User) WHERE a.token = $token CREATE (" + r.getName().replaceAll(" ", "") + ":Recipe {" +
                                "name: $name, " +
                                "description: $description, " +
                                "steps: $steps, " +
                                "image: $image, " +
                                "dateCreated: date($dateCreated), " +
                                "country: $country, " +
                                "difficulty: $difficulty, " +
                                "tags: $tags, " +
                                "ingredients: $ingredients} )-[created:Created]->(a)",
                                parameters(
                                        "token", token,
                                        "name", r.getName(),
                                        "description", r.getDescription(),
                                        "steps",r.getSteps(),
                                        "image",r.getImage(),
                                        "dateCreated", dateCreated,
                                        "country",r.getCountry(),
                                        "difficulty", r.getDifficulty(),
                                        "tags", r.getTags(),
                                        "ingredients", r.getIngredients()
                                )
                        );
                                tx.run(query);
                return null;
            });

            closeSession(session); //Cerramos la sesión
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }

    }
    //temporal para el main
    public ArrayList<Recipe> retrieveAllRecipes() {

        ArrayList<Recipe> listRecipes = new ArrayList<>();

        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (n1:User)-[:Created]-(n2:Recipe) RETURN n2.name, n2.description, n2.steps, n2.image, n2.dateCreated, n2.country, n1.username, n2.difficulty, n2.tags, n2.ingredients, ID(n2);");
            Result result = session.run(query);
            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next(); //Guardamos el registro
                //recogemos los valores
                String name = record.get(0).asString();
                String description = record.get(1).asString();
                List<Object> listSteps = record.get(2).asList();
                //convertimos la lista de objetos a array list de strings
                ArrayList<String> arrayListSteps = new ArrayList<>();
                for (Object obj : listSteps) {
                    arrayListSteps.add(obj.toString());
                }
                String image = record.get(3).asString();
                String dateCreated = record.get(4).asLocalDate().toString();
                String country = record.get(5).asString();
                String creator = record.get(6).asString();
                int difficulty = record.get(7).asInt();
                List<Object> listTags = record.get(8).asList();
                ArrayList<String> arrayListTags = new ArrayList<>();
                for (Object obj : listTags) {
                    arrayListTags.add(obj.toString());
                }
                List<Object> listIngredients = record.get(9).asList();
                ArrayList<String> arrayListIngredients = new ArrayList<>();
                for (Object obj : listIngredients) {
                    arrayListIngredients.add(obj.toString());
                }
                int idRecipe = record.get(10).asInt();
                //creamos una receta nueva
                Recipe recipe = new Recipe(name, description, arrayListSteps, dateCreated, difficulty, creator, image, country, arrayListTags, arrayListIngredients, idRecipe);
                listRecipes.add(recipe);
            }
            closeSession(session); //Cerramos la sesión
            return listRecipes; //Retornamos el valor
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }
    }

    public ArrayList<Recipe> retrieveAllRecipesbyUid(String uid) {

        ArrayList<Recipe> listRecipes = new ArrayList<>();

        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (n1:User)-[:Created]-(n2:Recipe) WHERE n1.token = '" + uid + "' RETURN n1.username, n2;");
            Result result = session.run(query);

            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next();
                String creator = record.get(0).asString();
                Node node = record.get(1).asNode(); //Guardamos el registro
                //recogemos los valores
                String name = node.get("name").asString();
                String description = node.get("description").asString();
                List<Object> listSteps = node.get("steps").asList();
                //convertimos la lista de objetos a array list de strings
                ArrayList<String> arrayListSteps = new ArrayList<>();
                for (Object obj : listSteps) {
                    arrayListSteps.add(obj.toString());
                }
                String image = node.get("image").asString();
                String dateCreated = node.get("dateCreated").asLocalDate().toString();
                String country = node.get("country").asString();

                int difficulty = node.get("difficulty").asInt();
                List<Object> listTags = node.get("tags").asList();
                ArrayList<String> arrayListTags = new ArrayList<>();
                for (Object obj : listTags) {
                    arrayListTags.add(obj.toString());
                }
                List<Object> listIngredients = node.get("ingredients").asList();
                ArrayList<String> arrayListIngredients = new ArrayList<>();
                for (Object obj : listIngredients) {
                    arrayListIngredients.add(obj.toString());
                }
                //creamos una receta nueva
                Recipe recipe = new Recipe(name, description, arrayListSteps, dateCreated, difficulty, creator, image, country, arrayListTags, arrayListIngredients); //Lo mostramos segun su tipo
                listRecipes.add(recipe);
            }
            closeSession(session); //Cerramos la sesión
            return listRecipes;
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }
    }

    public HashMap<String, String> retrieveCommentsbyUid(String uid) {

        HashMap<String, String> comments = new HashMap<>();

        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (n:User)-[n1:Commented]->(n2:User) WHERE n2.token = '" + uid + "' RETURN n.token, n1.comment;");
            Result result = session.run(query);
            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next(); //Guardamos el registro
                String id = record.get(0).asString();
                String comment = record.get(1).asString();
                comments.put(id, comment);
            }
            closeSession(session); //Cerramos la sesión
            return comments; //Retornamos el valor
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }
    }

    public static User retrieveUserbyUid(String uid) {

        User user = null;

        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (n:User) WHERE n.token = '" + uid + "' RETURN n.username, n.biography, n.imgProfile;");
            Result result = session.run(query);
            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next(); //Guardamos el registro
                String name = record.get(0).asString();
                String biography = record.get(1).asString();
                String imgProfile = record.get(2).asString();
                user = new User(name, biography, imgProfile);
            }

            closeSession(session); //Cerramos la sesión
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

    public String retrieveNameCurrentUser(String uid) {

        String userName = null;

        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (n:User) WHERE n.token = '" + uid + "' RETURN n.username;");
            Result result = session.run(query);
            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next(); //Guardamos el registro
                userName = record.get(0).asString();

            }
            closeSession(session); //Cerramos la sesión
            return userName; //Retornamos el valor
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }

    }

    public void commentRecipe(int rid, String uid, String comment, float rating) {

        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();
            //fecha
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateCreated = sdf.format(c.getTime());

            session.writeTransaction(tx -> {
                Query query = new Query("MATCH (u:User),(r:Recipe) WHERE ID(r) =" + rid +" AND u.token = '" + uid + "'  CREATE (u)-[c:Commented{comment:'" + comment + "',rating:"+ rating +", dateCreated:'"+dateCreated+"'}]->(r) RETURN type(c)\n");
                tx.run(query);
                return null;
            });

            closeSession(session); //Cerramos la sesión
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }

    }

    public void reportRecipe(int rid, String uid, String comment) {

        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();
            //fecha
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateCreated = sdf.format(c.getTime());

            session.writeTransaction(tx -> {
                Query query = new Query("MATCH (u:User),(r:Recipe) WHERE ID(r) =" + rid + " AND u.token = '" + uid + "'  CREATE (u)-[c:Reported{comment:'" + comment + "', dateCreated:'"+dateCreated+"'}]->(r) RETURN type(c)");
                tx.run(query);
                return null;
            });

            closeSession(session); //Cerramos la sesión
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }

    }

    public void commentUser(String senderId, String receiverId, String comment) {
        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();
            //fecha
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateCreated = sdf.format(c.getTime());

            session.writeTransaction(tx -> {
                Query query = new Query("MATCH (u:User),(r:User) WHERE u.token ='" + senderId +"' AND r.token = '" + receiverId + "'  CREATE (u)-[c:Commented{comment:'" + comment + "', dateCreated:'"+dateCreated+"'}]->(r) RETURN type(c)");
                tx.run(query);
                return null;
            });

            closeSession(session); //Cerramos la sesión
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }
    }


    public boolean isLiked(int rid, String uid){
        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();
            //fecha
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateCreated = sdf.format(c.getTime());

            List<Record> nResult = session.run("MATCH (u:User)-[c:Liked]->(r:Recipe) WHERE ID(r) = "+rid+" AND u.token = '"+uid+"'  RETURN ID(c)")
                    .list();
            if(nResult.size() != 0){
                closeSession(session); //Cerramos la sesión
                return true;
            }else{
                closeSession(session); //Cerramos la sesión
                return false;
            }


            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }
    }
    public boolean likeRecipe(int rid, String uid) {

        try {
            //Iniciamos una sesion con la bd
            Session session = openSession();
            //fecha
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateCreated = sdf.format(c.getTime());

                List<Record> nResult = session.run("MATCH (u:User)-[c:Liked]->(r:Recipe) WHERE ID(r) = "+rid+" AND u.token = '"+uid+"'  RETURN ID(c)")
                        .list();
                if(nResult.size() != 0){
                    int id = nResult.get(0).get(0).asInt();
                    session.writeTransaction(tx -> {
                        Query query = new Query("MATCH (u:User)-[c:Liked]->(r:Recipe) WHERE ID(r) = "+rid+" AND u.token = '"+uid+"' AND ID(c) = "+id+"  DELETE c");
                        tx.run(query);
                        return false;
                    });
                    closeSession(session); //Cerramos la sesión
                    return false;
                }else{
                    session.writeTransaction(tx -> {
                        Query query = new Query("MATCH (u:User),(r:Recipe) WHERE ID(r) =" + rid +" AND u.token = '" + uid + "'  CREATE (u)-[c:Liked{dateCreated:'"+dateCreated+"'}]->(r) RETURN type(c)");
                        tx.run(query);
                        return false;
                    });
                    closeSession(session); //Cerramos la sesión
                    return true;
                }


            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        }
    }

    public ArrayList<Comment> getCommentsOnRecipe(int rid) {

        ArrayList<Comment> listComments = new ArrayList<>();

        try {
            //Iniciamos una sesion con la bd (el driver se configura en el constructor)
            Session session = openSession();
            //Creamos la sentencia que se ejecutara y guardamos el resultado
            Query query = new Query("MATCH (a:Recipe)-[r:Commented]-(b:User) WHERE id(a) = "+rid+" RETURN r.comment, r.rating,r.dateCreated, b.token");
            Result result = session.run(query);

            while (result.hasNext()) //Mientras haya registros..
            {
                Record record = result.next();
                //recogemos los valores
                String comment = record.get(0).asString();
                float rating = record.get(1).asFloat();
                String dateCreated = record.get(2).asString();
                String tokenUser = record.get(3).asString();

                //creamos una receta nueva
                Comment c = new Comment(comment, dateCreated, rating, tokenUser);
                listComments.add(c);
            }
            closeSession(session); //Cerramos la sesión
            return listComments;
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, " raised an exception", ex);
            throw ex;
        } catch (NoSuchRecordException ex) {
            LOGGER.log(Level.SEVERE, "No record found raised an exception", ex);
            throw ex;
        }

    }

    /*
    MATCH (n:Users) WHERE n.username = '" + username + "' AND n.password = '" + password + "' RETURN CASE WHEN n IS NOT NULL THEN true ELSE false END AS n;
    MATCH (n:Users) WHERE n.username = $username AND n.password = $password RETURN CASE WHEN n IS NOT NULL THEN true ELSE false END AS n; //Devolver BOOLEAN si se encuentra o no
    MATCH (n:Users) WHERE n.username = $username AND n.password = $password RETURN
    CREATE (user3:Users {name:'user3', username:'user3', password:'user3', followers:[], following:[], recipes:[], biography:'', photos:[], videos:[], perfilimg:''}); //Sentencia para crear users
    match (c) where id(c) = 16 set c.image = "" return c
    //usuario comenta receta
    MATCH (u:User),(r:Recipe) WHERE ID(r) = 18 AND u.token = "xmg10sMQgMS4392zORWGW7TQ1Qg2"  CREATE (u)-[c:Commented{comment:"texto del comentario",rating:5.0}]->(r) RETURN type(c)
    //borrar comenatrio
    MATCH (n:User {UserName: 'test'})-[r:Commented]->() DELETE r
    //usuario comenta usuario
    MATCH (u:User),(r:User) WHERE u.token ="xmg10sMQgMS4392zORWGW7TQ1Qg2" AND r.token = "jj85oho2sXgPMNuQKkTORWs8gVF2"  CREATE (u)-[c:Commented{comment:"maquina!", dateCreated:"2023-03-08"}]->(r) RETURN type(c)
    //comentarios de una receta
    MATCH (a:Recipe)-[r:Commented]-(b:User) WHERE id(a) = 18 RETURN r
    */
}


