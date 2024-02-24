package com.example.databaseperformancedemo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.hibernate.Session;

/**
 * Demo class to compare the performance of raw JDBC and Hibernate/JPA for database access.
 */
public class DatabasePerformanceDemo {

	// JDBC connection parameters
	// Modify the values below to match your MySQL database configuration
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/<your-db-name>";
	private static final String JDBC_USER = "<mysql-username>";
	private static final String JDBC_PASSWORD = "<mysql-passwordname>";


    // Hibernate/JPA connection parameters
    private static final String PERSISTENCE_UNIT_NAME = "persistenceUnitName";

    /**
     * Main method to run the performance comparison between raw JDBC and Hibernate/JPA.
     */
    public static void main(String[] args) {
        // Number of iterations for performance comparison
        int iterations = 5;

        // Total execution time for raw JDBC and Hibernate/JPA
        long rawJdbcTotalTime = 0;
        long hibernateJpaTotalTime = 0;

        // Perform multiple iterations to calculate average execution time
        for (int i = 0; i < iterations; i++) {
            // Measure execution time for raw JDBC
            rawJdbcTotalTime += measureExecutionTime(DatabasePerformanceDemo::testRawJDBC);

            // Measure execution time for Hibernate/JPA
            hibernateJpaTotalTime += measureExecutionTime(DatabasePerformanceDemo::testHibernateJPA);
        }

        // Calculate average execution time for raw JDBC and Hibernate/JPA
        long rawJdbcAverageTime = rawJdbcTotalTime / iterations;
        long hibernateJpaAverageTime = hibernateJpaTotalTime / iterations;

        // Display average execution time and performance difference
        System.out.println("Raw JDBC average execution time: " + rawJdbcAverageTime + " nanoseconds");
        System.out.println("Hibernate/JPA average execution time: " + hibernateJpaAverageTime + " nanoseconds");
        System.out.println("Performance difference (Raw JDBC - Hibernate/JPA): " +
                (rawJdbcAverageTime - hibernateJpaAverageTime) + " nanoseconds");
    }

    /**
     * Method to measure the execution time of a given task.
     */
    private static long measureExecutionTime(Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Method to perform database access using raw JDBC.
     */
    private static void testRawJDBC() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM city")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Process each row retrieved from the database
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to perform database access using Hibernate/JPA.
     */
    private static void testHibernateJPA() {
        // Create an EntityManagerFactory using Hibernate
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Obtain a Hibernate Session from the EntityManager
            Session session = entityManager.unwrap(Session.class);

            // Execute a query using Hibernate's JPQL (Java Persistence Query Language)
            TypedQuery<City> query = session.createQuery("SELECT c FROM City c", City.class);
            List<City> results = query.getResultList();
            for (City city : results) {
                // Process each city retrieved from the database
            }
        } finally {
            // Close the EntityManager and EntityManagerFactory to release resources
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}
