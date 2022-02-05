/******************************************************
 * File: PersonDaoImpl.java Course materials (22W) CST8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 */
package databank.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import databank.model.PersonPojo;

@SuppressWarnings("unused")
/**
 * Description: Implements the C-R-U-D API for the database
 */
//TODO don't forget this object is a managed bean with a application scope
@Named
@ApplicationScoped
public class PersonDaoImpl implements PersonDao, Serializable {
	/** explicitly set serialVersionUID */
	private static final long serialVersionUID = 1L;

	private static final String DATABANK_DS_JNDI = "java:app/jdbc/databank";
	private static final String READ_ALL = "select * from person";
	private static final String READ_PERSON_BY_ID = "select * from person where id = ?";
	private static final String INSERT_PERSON = "insert into person (last_name,first_name,email,phone,city,created) values(?,?,?,?,?,?)";
	private static final String UPDATE_PERSON_ALL_FIELDS = "update person set last_name = ?, first_name = ?, email = ?, phone = ?, city = ? where id = ?";
	private static final String DELETE_PERSON_BY_ID = "delete from person where id = ?";

	@Inject
	protected ExternalContext externalContext;

	private void logMsg( String msg) {
		( (ServletContext) externalContext.getContext()).log( msg);
	}

	@Resource( lookup = DATABANK_DS_JNDI)
	protected DataSource databankDS;

	protected Connection conn;
	protected PreparedStatement readAllPstmt;
	protected PreparedStatement readByIdPstmt;
	protected PreparedStatement createPstmt;
	protected PreparedStatement updatePstmt;
	protected PreparedStatement deleteByIdPstmt;

	@PostConstruct
	protected void buildConnectionAndStatements() {
		try {
			logMsg( "building connection and stmts");
			conn = databankDS.getConnection();
			readAllPstmt = conn.prepareStatement( READ_ALL);
			createPstmt = conn.prepareStatement( INSERT_PERSON, RETURN_GENERATED_KEYS);
			//TODO initialize other PreparedStatements
			readByIdPstmt = conn.prepareStatement( READ_PERSON_BY_ID);
			updatePstmt = conn.prepareStatement( UPDATE_PERSON_ALL_FIELDS);
			deleteByIdPstmt = conn.prepareStatement( DELETE_PERSON_BY_ID);
		} catch ( Exception e) {
			logMsg( "something went wrong getting connection from database: " + e.getLocalizedMessage());
		}
	}

	@PreDestroy
	protected void closeConnectionAndStatements() {
		try {
			logMsg( "closing stmts and connection");
			readAllPstmt.close();
			createPstmt.close();
			//TODO close other PreparedStatements
			readByIdPstmt.close();
			updatePstmt.close();
			deleteByIdPstmt.close();
			conn.close();
		} catch ( Exception e) {
			logMsg( "something went wrong closing stmts or connection: " + e.getLocalizedMessage());
		}
	}

	@Override
	public List< PersonPojo> readAllPeople() {
		logMsg( "reading all People");
		List< PersonPojo> people = new ArrayList<>();
		try ( ResultSet rs = readAllPstmt.executeQuery();) {

			while ( rs.next()) {
				PersonPojo newPerson = new PersonPojo();
				newPerson.setId( rs.getInt( "id"));
				newPerson.setFirstName( rs.getString( "first_name"));
				//TODO complete the person initialization
				newPerson.setLastName(rs.getString( "last_name"));
				newPerson.setEmail(rs.getString( "email"));
				newPerson.setPhoneNumber(rs.getString( "phone"));
				newPerson.setCity( rs.getString( "city"));
				newPerson.setCreated( rs.getTimestamp( "created").toLocalDateTime());
				people.add( newPerson);
			}
		} catch ( SQLException e) {
			logMsg( "something went wrong accessing database: " + e.getLocalizedMessage());
		}
		return people;
	}

	@Override
	public PersonPojo createPerson( PersonPojo person) {
		logMsg( "creating an person");
		//TODO complete the insertion of a new person
		//TODO use try-and-catch statement
		PersonPojo newPerson = new PersonPojo();
		try {
			createPstmt.setString(1, person.getLastName());
			createPstmt.setString(2, person.getFirstName());
			createPstmt.setString(3, person.getEmail());
			createPstmt.setString(4, person.getPhoneNumber());
			createPstmt.setString(5, person.getCity());
			createPstmt.setObject(6, person.getCreated());
			createPstmt.execute();
			
			ResultSet rs = createPstmt.getGeneratedKeys();
			int generatedKey = 0;
			if (rs.next()) {
			    generatedKey = rs.getInt(1);
			}
			newPerson.setId(generatedKey);
			newPerson.setLastName(person.getLastName());
			newPerson.setFirstName(person.getFirstName());
			newPerson.setEmail(person.getEmail());
			newPerson.setPhoneNumber(person.getPhoneNumber());
			newPerson.setCity(person.getCity());
			newPerson.setCreated(person.getCreated());
		} catch (SQLException e) {
            logMsg("something went wrong creating data: " + e.getLocalizedMessage());
    	}	
		return newPerson;
	}

	@Override
	public PersonPojo readPersonById(int personId) {
		logMsg("read a specific person");
		// TODO complete the retrieval of a specific person by its id
		// TODO use try-and-catch statement
		PersonPojo person = new PersonPojo();
		try {
			readByIdPstmt.setInt(1, personId);
			ResultSet rs = readByIdPstmt.executeQuery();
			while (rs.next()) {
				person.setId(rs.getInt("id"));
				person.setLastName(rs.getString("last_name"));
				person.setFirstName(rs.getString("first_name"));
				person.setEmail(rs.getString("email"));
				person.setPhoneNumber(rs.getString("phone"));
				person.setCity(rs.getString("city"));
				person.setCreated(rs.getTimestamp("created").toLocalDateTime());
			}
		} catch (SQLException e) {
			logMsg("something went wrong reading PersonById: " + e.getLocalizedMessage());
		}
		return person;
	}

	@Override
	public void updatePerson( PersonPojo person) {
		logMsg( "updating a specific person");
		//TODO complete the update of a specific person
		//TODO use try-and-catch statement
	 	try {
	 		updatePstmt.setString(1, person.getLastName());
    		updatePstmt.setString(2, person.getFirstName());
    		updatePstmt.setString(3, person.getEmail());
    		updatePstmt.setString(4, person.getPhoneNumber());
    		updatePstmt.setString(5, person.getCity());
    		updatePstmt.setInt(6, person.getId());
    		updatePstmt.executeUpdate();
    	}
    	catch (SQLException e) {
            logMsg("something went wrong updating database: " + e.getLocalizedMessage());
    	}
	}

	@Override
	public void deletePersonById(int personId) {
		logMsg("deleting a specific person");
		// TODO complete the deletion of a specific person
		// TODO use try-and-catch statement
		try {
			deleteByIdPstmt.setInt(1, personId);
			deleteByIdPstmt.execute();
		} catch (SQLException e) {
			logMsg("something went wrong deleting database: " + e.getLocalizedMessage());
		}
	}

}