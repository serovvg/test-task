package com.haulmont.testtask.dao;

import com.haulmont.testtask.model.Doctor;
import com.haulmont.testtask.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;

public class DoctorDAO  {

    private static DoctorDAO instance;
    private final Connection connection;

    public static DoctorDAO getInstance(){
        if(instance == null){
            instance = new DoctorDAO();
        }
        return instance;
    }
    private DoctorDAO() {
        this.connection = ConnectionUtil.getInstance();
    }

    public void create(Doctor doctor) {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.INSERT.QUERY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, doctor.getLastName());
            preparedStatement.setString(2, doctor.getFirstName());
            preparedStatement.setString(3, doctor.getPatronymic());
            preparedStatement.setString(4, doctor.getSpecialization());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                doctor.setId(resultSet.getLong(1));
            }
            resultSet.close();
            preparedStatement.close();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Doctor read(long id) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.GET.QUERY)){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                doctor.setLastName(resultSet.getString("last_name"));
                doctor.setFirstName(resultSet.getString("first_name"));
                doctor.setPatronymic(resultSet.getString("patronymic"));
                doctor.setSpecialization(resultSet.getString("specialization"));
            } else {
                doctor.setId(-1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctor;
    }

    public ArrayList<Doctor> readAll() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(SQLQuery.GET_ALL.QUERY);
            Doctor doctor;
            while (resultSet.next()){
                doctor = new Doctor();
                doctor.setId(resultSet.getLong("id"));
                doctor.setLastName(resultSet.getString("last_name"));
                doctor.setFirstName(resultSet.getString("first_name"));
                doctor.setPatronymic(resultSet.getString("patronymic"));
                doctor.setSpecialization(resultSet.getString("specialization"));
                doctor.setRecipesQuantity(resultSet.getInt("recipes"));
                doctors.add(doctor);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return doctors;
    }

    public void update(Doctor doctor) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.UPDATE.QUERY)){
            preparedStatement.setString(1, doctor.getLastName());
            preparedStatement.setString(2, doctor.getFirstName());
            preparedStatement.setString(3, doctor.getPatronymic());
            preparedStatement.setString(4, doctor.getSpecialization());
            preparedStatement.setLong(5, doctor.getId());
            preparedStatement.executeUpdate();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(long id) throws SQLIntegrityConstraintViolationException{
        try(PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.DELETE.QUERY)){
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        } catch (SQLIntegrityConstraintViolationException e){
            throw new SQLIntegrityConstraintViolationException("Невозможно удалить выбранную запись, так как она используется в таблице \"Рецепты\".");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    enum SQLQuery{
        GET("SELECT * FROM doctors WHERE id = (?)"),
        GET_ALL("SELECT doctors.*, count(recipes.id) as recipes " +
                "FROM doctors " +
                "LEFT JOIN recipes ON doctors.id = recipes.doctor_id " +
                "GROUP BY doctors.id"),
        INSERT("INSERT INTO doctors (last_name, first_name, patronymic, specialization) VALUES ((?),(?),(?),(?))"),
        UPDATE("UPDATE doctors SET last_name = (?), first_name = (?), patronymic = (?), " +
                "specialization = (?) WHERE id = (?)"),
        DELETE("DELETE FROM doctors WHERE id = (?)"),
        CHECKPOINT("CHECKPOINT");
        String QUERY;
        SQLQuery(String QUERY) {
            this.QUERY = QUERY;
        }
    }


}
