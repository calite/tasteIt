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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BdConnection {
    //NEO4J
    private final String uri = "neo4j+s://dc95b24b.databases.neo4j.io"; //URL conexion Neo4j
    private final String user = "neo4j";
    private final String pass = "sBQ6Fj2oXaFltjizpmTDhyEO9GDiqGM1rG-zelf17kg"; //PDTE CIFRAR
    //FIN NEO
}


