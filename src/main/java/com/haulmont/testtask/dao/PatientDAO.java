package com.haulmont.testtask.dao;

import com.haulmont.testtask.model.Patient;
import com.haulmont.testtask.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;

public class PatientDAO {

    private static PatientDAO instance;
    private final Connection connection;

    public static PatientDAO getInstance(){
        if(instance == null){
            instance = new PatientDAO();
        }
        return instance;
    }
    private PatientDAO() {
        this.connection = ConnectionUtil.getInstance();
    }

    public void create(Patient patient) {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.INSERT.QUERY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, patient.getLastName());
            preparedStatement.setString(2, patient.getFirstName());
            preparedStatement.setString(3, patient.getPatronymic());
            preparedStatement.setString(4, patient.getPhoneNumber());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                patient.setId(resultSet.getLong(1));
            }
            resultSet.close();
            preparedStatement.close();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(DoctorDAO.SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Patient read(long id) {
        Patient patient = new Patient();
        patient.setId(id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.GET.QUERY)){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                patient.setLastName(resultSet.getString("last_name"));
                patient.setFirstName(resultSet.getString("first_name"));
                patient.setPatronymic(resultSet.getString("patronymic"));
                patient.setPhoneNumber(resultSet.getString("phone_number"));
            } else {
                patient.setId(-1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    public ArrayList<Patient> readAll() {
        ArrayList<Patient> patients = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(SQLQuery.GET_ALL.QUERY);
            Patient patient;
            while (resultSet.next()){
                patient = new Patient();
                patient.setId(resultSet.getLong("id"));
                patient.setLastName(resultSet.getString("last_name"));
                patient.setFirstName(resultSet.getString("first_name"));
                patient.setPatronymic(resultSet.getString("patronymic"));
                patient.setPhoneNumber(resultSet.getString("phone_number"));
                patients.add(patient);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return patients;
    }

    public void update(Patient patient) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.UPDATE.QUERY)){
            preparedStatement.setString(1, patient.getLastName());
            preparedStatement.setString(2, patient.getFirstName());
            preparedStatement.setString(3, patient.getPatronymic());
            preparedStatement.setString(4, patient.getPhoneNumber());
            preparedStatement.setLong(5, patient.getId());
            preparedStatement.executeUpdate();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(DoctorDAO.SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(long id) throws SQLIntegrityConstraintViolationException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.DELETE.QUERY)){
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(DoctorDAO.SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        }catch (SQLIntegrityConstraintViolationException e){
            throw new SQLIntegrityConstraintViolationException("Невозможно удалить выбранную запись, так как она используется в таблице \"Рецепты\".");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    enum SQLQuery{
        GET("SELECT * FROM patients WHERE id = (?)"),
        GET_ALL("SELECT * FROM patients"),
        INSERT("INSERT INTO patients (last_name, first_name, patronymic, phone_number) VALUES ((?),(?),(?),(?))"),
        UPDATE("UPDATE patients SET last_name = (?), first_name = (?), patronymic = (?), " +
                "phone_number = (?) WHERE id = (?)"),
        DELETE("DELETE FROM patients WHERE id = (?)"),
        CHECKPOINT("CHECKPOINT");
        String QUERY;
        SQLQuery(String QUERY) {
            this.QUERY = QUERY;
        }
    }

}
