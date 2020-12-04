package com.haulmont.testtask.dao;

import com.haulmont.testtask.model.Priority;
import com.haulmont.testtask.model.Recipe;
import com.haulmont.testtask.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;

public class RecipeDAO {

    private static RecipeDAO instance;
    private final Connection connection;
    private DoctorDAO doctorDAO;
    private PatientDAO patientDAO;

    public static RecipeDAO getInstance(){
        if(instance == null){
            instance = new RecipeDAO();
            instance.doctorDAO = DoctorDAO.getInstance();
            instance.patientDAO = PatientDAO.getInstance();
        }
        return instance;
    }
    private RecipeDAO() {
        this.connection = ConnectionUtil.getInstance();
    }

    public void create(Recipe recipe) {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(RecipeDAO.SQLQuery.INSERT.QUERY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, recipe.getDescription());
            preparedStatement.setLong(2, recipe.getPatient().getId());
            preparedStatement.setLong(3, recipe.getDoctor().getId());
            preparedStatement.setDate(4, recipe.getCreationDate());
            preparedStatement.setShort(5, recipe.getValidity());
            preparedStatement.setString(6, recipe.getPriority().getDbTitle());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                recipe.setId(resultSet.getLong(1));
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

    public Recipe read(long id) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(RecipeDAO.SQLQuery.GET.QUERY)){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                recipe.setDescription(resultSet.getString("description"));
                recipe.setPatient(patientDAO.read(resultSet.getLong("patient_id")));
                recipe.setDoctor(doctorDAO.read(resultSet.getLong("doctor_id")));
                recipe.setCreationDate(resultSet.getDate("creation_date"));
                recipe.setValidity(resultSet.getShort("validity"));
                recipe.setPriority(Priority.valueOf(resultSet.getString("priority")));
            } else {
                recipe.setId(-1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe;
    }

    public ArrayList<Recipe> readAll() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(RecipeDAO.SQLQuery.GET_ALL.QUERY);
            Recipe recipe;
            while (resultSet.next()){
                recipe = new Recipe();
                recipe.setId(resultSet.getLong("id"));
                recipe.setDescription(resultSet.getString("description"));
                recipe.setPatient(patientDAO.read(resultSet.getLong("patient_id")));
                recipe.setDoctor(doctorDAO.read(resultSet.getLong("doctor_id")));
                recipe.setCreationDate(resultSet.getDate("creation_date"));
                recipe.setValidity(resultSet.getShort("validity"));
                recipe.setPriority(Priority.valueOf(resultSet.getString("priority")));
                recipes.add(recipe);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return recipes;
    }

    public void update(Recipe recipe) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(RecipeDAO.SQLQuery.UPDATE.QUERY)){
            preparedStatement.setString(1, recipe.getDescription());
            preparedStatement.setLong(2, recipe.getPatient().getId());
            preparedStatement.setLong(3, recipe.getDoctor().getId());
            preparedStatement.setDate(4, recipe.getCreationDate());
            preparedStatement.setShort(5, recipe.getValidity());
            preparedStatement.setString(6, recipe.getPriority().getDbTitle());
            preparedStatement.setLong(7, recipe.getId());
            preparedStatement.executeUpdate();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(DoctorDAO.SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(RecipeDAO.SQLQuery.DELETE.QUERY)){
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            Statement checkpointStatement = connection.createStatement();
            checkpointStatement.executeQuery(DoctorDAO.SQLQuery.CHECKPOINT.QUERY);
            checkpointStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    enum SQLQuery{
        GET("SELECT * FROM recipes  WHERE id = (?)"),
        GET_ALL("SELECT * FROM recipes"),
        INSERT("INSERT INTO recipes (description, patient_id, doctor_id, creation_date, validity, priority) " +
                "VALUES ((?),(?),(?),(?),(?),(?))"),
        UPDATE("UPDATE recipes SET description = (?), patient_id = (?), doctor_id = (?), creation_date = (?), " +
                "validity = (?), priority = (?) WHERE id = (?)"),
        DELETE("DELETE FROM recipes WHERE id = (?)"),
        CHECKPOINT("CHECKPOINT");
        String QUERY;
        SQLQuery(String QUERY) {
            this.QUERY = QUERY;
        }
    }

}
