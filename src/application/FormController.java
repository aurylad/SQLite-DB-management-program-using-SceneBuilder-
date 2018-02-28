package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class FormController implements Initializable {

	public Model model = new Model();
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private ObservableList<AlbumList>observableList = FXCollections.observableArrayList();
	private ObservableList<EmployeeList>employeesObservableList = FXCollections.observableArrayList();
	
	//Table Albums
	
	@FXML Label statusLabel;
	@FXML Label dateLabel;
	@FXML Label albumIdLabel;
	@FXML Label titleLabel;
	@FXML Label artistIdLabel;
	@FXML Button buttonUpdate;
	@FXML Button buttonDelete;
	@FXML Button buttonAdd;
	@FXML Separator seperator;
	@FXML TableView<AlbumList> tableAlbums;
	@FXML TableColumn<?, ?> columnAlbumId;
	@FXML TableColumn<?, ?> columnTitle;
	@FXML TableColumn<?, ?> columnArtistId;
	@FXML TextField txtFieldAlbumId;
	@FXML TextField txtFieldTitle;
	@FXML TextField txtFieldArtistId;
	
	//Table Employees
	@FXML TableView<EmployeeList> tableEmployee;
	@FXML TableColumn<?, ?> columnEmployeeId;
	@FXML TableColumn<?, ?> columnLastName;
	@FXML TableColumn<?, ?> columnFirstName;
	@FXML TableColumn<?, ?> columnSecondTitle;
	@FXML TextField txtFieldEmployeeId;
	@FXML TextField txtFieldFirstName;
	@FXML TextField txtFieldLastName;
	@FXML TextField txtFieldSecondTitle;
	@FXML Label employeeIdLabel;
	@FXML Label lastNameLabel;
	@FXML Label firstNameLabel;
	@FXML Separator secondSeparator;
	@FXML Button buttonUpdateEmployees;
	@FXML Button buttonDeleteEmployees;
	@FXML Button buttonAddEmployees;
	@FXML Label EmployeesTitleLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		if(model.isDbConnected()){
			statusLabel.setText("Database connected!");
		}else{
			statusLabel.setText("Database not connected!");
		}
		
		setCellsAlbumTable();
		loadDataFromDatabaseAlbums();
		setValueToAlbumTextFields();
		
		setCellsEmployeeTable();
		loadDataFromDatabaseEmployees();
		setValueToEmployeesTextFields();
	}

 //======================Album table methods (first TAB)==============================//
	
	private void setCellsAlbumTable(){
		columnAlbumId.setCellValueFactory(new PropertyValueFactory<>("albumId"));
		columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		columnArtistId.setCellValueFactory(new PropertyValueFactory<>("artistId"));
	}
	

	private void loadDataFromDatabaseAlbums(){
		try {
			preparedStatement = model.connection.prepareStatement("Select * from albums");
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				observableList.add(new AlbumList(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		tableAlbums.setItems(observableList);
	}

	
	private void setValueToAlbumTextFields(){

		txtFieldAlbumId.setEditable(false);
		tableAlbums.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			AlbumList albumList = tableAlbums.getItems().get(tableAlbums.getSelectionModel().getSelectedIndex());
			txtFieldAlbumId.setText(""+albumList.getAlbumId());
			txtFieldTitle.setText(albumList.getTitle());
			txtFieldArtistId.setText(""+albumList.getArtistId());
			statusLabel.setText(albumList.getAlbumId() +"/"+ albumList.getTitle() +"/"+ albumList.getArtistId());
			setDate();
		});
	}
	
	
	public void udateAlbumTable () {
		
		if (txtFieldTitle.getText().isEmpty() || txtFieldArtistId.getText().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Empty fields found!");
			alert.setContentText("Please fill required fields");
			alert.showAndWait();
		} else {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Do you really want to update this record?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			String sql = "Update albums set Title = ?, ArtistId = ? where AlbumId = ?";
			try {
				int albumId = Integer.valueOf(txtFieldAlbumId.getText());
				String title = txtFieldTitle.getText();
				int artistId = Integer.valueOf(txtFieldArtistId.getText());
				preparedStatement = model.connection.prepareStatement(sql);
				preparedStatement.setString(1, title);
				preparedStatement.setInt(2, artistId);
				preparedStatement.setInt(3, albumId);
				int i = preparedStatement.executeUpdate();
				
				if(i == 1){
					tableAlbums.getItems().clear();
					setCellsAlbumTable();
					loadDataFromDatabaseAlbums();
					setValueToAlbumTextFields();
					statusLabel.setText("Record - ("+ txtFieldAlbumId.getText()+ " / " +txtFieldTitle.getText()+ " / " +txtFieldArtistId.getText() + ") updated successfully !");
					setDate ();
				}
			} catch (SQLException e1) { e1.printStackTrace();}
		} else {
		    // CANCEL BUTTON PRESSED
		}
	  }
	}
	
	
	public void removeFromAlbumTable () {
		
		if (txtFieldTitle.getText().isEmpty() || txtFieldArtistId.getText().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Empty fields found!");
			alert.setContentText("Please choose which field you want to remove");
			alert.showAndWait();
		} else {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Do you really want to delete this record?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			String sql = "Delete from albums where AlbumId = ?";
			try {
				int albumId = Integer.valueOf(txtFieldAlbumId.getText());
				preparedStatement = model.connection.prepareStatement(sql);
				preparedStatement.setInt(1, albumId);
				int i = preparedStatement.executeUpdate();
				
				if(i == 1){
					tableAlbums.getItems().clear();
					setCellsAlbumTable();
					loadDataFromDatabaseAlbums();
					setValueToAlbumTextFields();
					statusLabel.setText("Record - ("+ txtFieldAlbumId.getText()+ " / " +txtFieldTitle.getText()+ " / " +txtFieldArtistId.getText() + ") deleted successfully !");
					setDate ();
				}
				
			} catch (SQLException e1) {e1.printStackTrace();}
		} else {
		    // CANCEL BUTTON PRESSED
		}
	  }
	}
	
	
	public void addToAlbumTable () {
		
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Add new record dialog");
		dialog.setHeaderText("Please fill in all fields");
		
		// Set the button types.
		ButtonType confirmButton = new ButtonType("Confirm", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);
		
		// Create the labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 100));

		TextField albumId = new TextField();
		albumId.setEditable(false);
		albumId.setPromptText("Auto increment number");
		
		TextField title = new TextField();
		title.setPromptText("");
		
		TextField artistId = new TextField();
		artistId.setPromptText("");
		
		grid.add(new Label("AlbumId:"), 0, 0);
		grid.add(albumId, 1, 0);
		grid.add(new Label("Title:"), 0, 1);
		grid.add(title, 1, 1);
		grid.add(new Label("ArtistId:"), 0, 2);
		grid.add(artistId, 1, 2);
		dialog.getDialogPane().setContent(grid);

		//Convert the results when the confirm button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == confirmButton) {
				return new Pair<>(title.getText(), artistId.getText());
			}
			return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		result.ifPresent(results -> {
		    String sql = "INSERT INTO albums (Title, ArtistId) VALUES (?, ?)";
			try {
				preparedStatement = model.connection.prepareStatement(sql);
				preparedStatement.setString(1, results.getKey());
				preparedStatement.setString(2, results.getValue());
				
				int i = preparedStatement.executeUpdate();
				if(i == 1){
					tableAlbums.getItems().clear();
					setCellsAlbumTable();
					loadDataFromDatabaseAlbums();
					setValueToAlbumTextFields();
					statusLabel.setText("Record - ("+ results.getKey()+ " / " +results.getValue()+  ") added successfully !");
					setDate ();
				}
			} catch (SQLException e1) {e1.printStackTrace();}
		});		
	}

	//======================Employees table methods (second TAB)==============================//

	private void setCellsEmployeeTable(){
		columnEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
		columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		columnSecondTitle.setCellValueFactory(new PropertyValueFactory<>("secondTitle"));
	}

	
	private void loadDataFromDatabaseEmployees(){
		try {
			preparedStatement = model.connection.prepareStatement("Select EmployeeId, LastName, FirstName, Title from employees");
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				employeesObservableList.add(new EmployeeList(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		tableEmployee.setItems(employeesObservableList);
	}

	
	private void setValueToEmployeesTextFields(){
	
		txtFieldEmployeeId.setEditable(false);
		tableEmployee.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			EmployeeList employeeList = tableEmployee.getItems().get(tableEmployee.getSelectionModel().getSelectedIndex());
			txtFieldEmployeeId.setText(""+employeeList.getEmployeeId());
			txtFieldFirstName.setText(employeeList.getFirstName());
			txtFieldLastName.setText(employeeList.getLastName());
			txtFieldSecondTitle.setText(employeeList.getSecondTitle());
			statusLabel.setText(employeeList.getEmployeeId() +"/"+ employeeList.getFirstName() +"/"+ employeeList.getLastName()+"/"+ employeeList.getSecondTitle());
			setDate();
		});
	}

	
	public void udateEmployeesTable () {
		
		if (txtFieldLastName.getText().isEmpty() || txtFieldFirstName.getText().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Empty fields found!");
			alert.setContentText("Please fill required fields");
			alert.showAndWait();
		} else {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Do you really want to update this record?");
	
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			String sql = "Update employees set LastName = ?, FirstName = ?, Title = ? where EmployeeId = ?";
			try {
				int employeeId = Integer.valueOf(txtFieldEmployeeId.getText());
				String lastName = txtFieldLastName.getText();
				String firstName = txtFieldFirstName.getText();
				String title = txtFieldSecondTitle.getText();
				preparedStatement = model.connection.prepareStatement(sql);
				preparedStatement.setString(1, lastName);
				preparedStatement.setString(2, firstName);
				preparedStatement.setString(3, title);
				preparedStatement.setInt(4, employeeId);
				
				int i = preparedStatement.executeUpdate();
				if(i == 1){
					tableEmployee.getItems().clear();
					setCellsEmployeeTable();
					loadDataFromDatabaseEmployees();
					setValueToEmployeesTextFields();
					statusLabel.setText("Record - ("+ txtFieldEmployeeId.getText()+ " / " +txtFieldLastName.getText()+ " / " +txtFieldFirstName.getText() + " / " +txtFieldSecondTitle.getText() + ") updated successfully !");
					setDate ();
				}
			} catch (SQLException e1) {e1.printStackTrace();}
		} else {
		    // CANCEL BUTTON PRESSED
		}
	  }
	}

	
	public void removeFromEmpployeesTable () {
		
		if (txtFieldLastName.getText().isEmpty() || txtFieldFirstName.getText().isEmpty() || txtFieldSecondTitle.getText().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Empty fields found!");
			alert.setContentText("Please choose which field you want to remove");
			alert.showAndWait();
		} else {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Do you really want to delete this record?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			String sql = "Delete from employees where EmployeeId = ?";
			try {
				int title = Integer.valueOf(txtFieldEmployeeId.getText());
				preparedStatement = model.connection.prepareStatement(sql);
				preparedStatement.setInt(1, title);
				int i = preparedStatement.executeUpdate();
				
				if(i == 1){
					tableEmployee.getItems().clear();
					setCellsEmployeeTable();
					loadDataFromDatabaseEmployees();
					setValueToEmployeesTextFields();
					statusLabel.setText("Record - ("+ txtFieldEmployeeId.getText()+ " / " +txtFieldLastName.getText()+ " / " +txtFieldFirstName.getText() + " / " +txtFieldSecondTitle.getText() + ") updated successfully !");
					setDate ();
				}
			} catch (SQLException e1) {e1.printStackTrace();}
		} else {
		    // CANCEL BUTTON PRESSED
		}
	  }
	}

	
	public void addToEmployeesTable () {
		
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Add new record dialog");
		dialog.setHeaderText("Please fill in all fields");
		
		// Set the button types.
		ButtonType confirmButton = new ButtonType("Confirm", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);
		
		// Create the labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 100));
	
		TextField employeeId = new TextField();
		employeeId.setEditable(false);
		employeeId.setPromptText("Auto increment number");
		
		TextField lastName = new TextField();
		lastName.setPromptText("");
		
		TextField firstName = new TextField();
		firstName.setPromptText("");
		
		TextField title = new TextField();
		title.setPromptText("");
		
		grid.add(new Label("EmployeeId:"), 0, 0);
		grid.add(employeeId, 1, 0);
		grid.add(new Label("LastName:"), 0, 1);
		grid.add(lastName, 1, 1);
		grid.add(new Label("FirstName:"), 0, 2);
		grid.add(firstName, 1, 2);
		grid.add(new Label("Title:"), 0, 3);
		grid.add(title, 1, 3);
		dialog.getDialogPane().setContent(grid);
	
		//Convert the results when the confirm button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == confirmButton) {
				return new Pair<>(lastName.getText(), firstName.getText());
			}
			return null;
		});
	
		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		result.ifPresent(results -> {
			String sql = "INSERT INTO Employees (LastName, FirstName, Title) VALUES (?, ?, ?)";
			try {
				preparedStatement = model.connection.prepareStatement(sql);
				preparedStatement.setString(1, lastName.getText());
				preparedStatement.setString(2, firstName.getText());
				preparedStatement.setString(3,  title.getText());
				int i = preparedStatement.executeUpdate();
				
				if(i == 1){
					tableEmployee.getItems().clear();
					setCellsEmployeeTable();
					loadDataFromDatabaseEmployees();
					setValueToEmployeesTextFields();
					statusLabel.setText("Record - ("+ lastName.getText()+ " / " +firstName.getText()+ " / " +title.getText()+  ") added successfully !");
					setDate ();
				}
				
			} catch (SQLException e1) {e1.printStackTrace();}
		});		
	}
	
	//======================Methods for both tables (both TABS)==============================//

	public void setDate (){
		String DT_FORMAT = "yyyy.MM.dd [(HH:mm:ss)]";
		this.dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DT_FORMAT)));
	}
	
	public void exitProgram (){
		Platform.exit();
	}
	
	public void hideForm (){
		albumIdLabel.setVisible(false);
		titleLabel.setVisible(false);
		artistIdLabel.setVisible(false);
		buttonUpdate.setVisible(false);
		buttonDelete.setVisible(false);
		buttonAdd.setVisible(false);
		seperator.setVisible(false);
		txtFieldAlbumId.setVisible(false);
		txtFieldTitle.setVisible(false);
		txtFieldArtistId.setVisible(false);
		txtFieldEmployeeId.setVisible(false);
		
		employeeIdLabel.setVisible(false);
		lastNameLabel.setVisible(false);
		firstNameLabel.setVisible(false);
		secondSeparator.setVisible(false);
		buttonUpdateEmployees.setVisible(false);
		buttonDeleteEmployees.setVisible(false);
		buttonAddEmployees.setVisible(false);
		EmployeesTitleLabel.setVisible(false);
		txtFieldEmployeeId.setVisible(false);
		txtFieldFirstName.setVisible(false);
		txtFieldLastName.setVisible(false);
		txtFieldSecondTitle.setVisible(false);
	}
	
	public void showForm (){
		statusLabel.setVisible(true);
		dateLabel.setVisible(true);
		albumIdLabel.setVisible(true);
		titleLabel.setVisible(true);
		artistIdLabel.setVisible(true);
		buttonUpdate.setVisible(true);
		buttonDelete.setVisible(true);
		buttonAdd.setVisible(true);
		seperator.setVisible(true);
		txtFieldAlbumId.setVisible(true);
		txtFieldTitle.setVisible(true);
		txtFieldArtistId.setVisible(true);
		
		employeeIdLabel.setVisible(true);
		lastNameLabel.setVisible(true);
		firstNameLabel.setVisible(true);
		secondSeparator.setVisible(true);
		buttonUpdateEmployees.setVisible(true);
		buttonDeleteEmployees.setVisible(true);
		buttonAddEmployees.setVisible(true);
		EmployeesTitleLabel.setVisible(true);
		txtFieldEmployeeId.setVisible(true);
		txtFieldFirstName.setVisible(true);
		txtFieldLastName.setVisible(true);
		txtFieldSecondTitle.setVisible(true);
	}
	
	public void makeNonEditableFields (){
		txtFieldAlbumId.setDisable(true);
		txtFieldTitle.setDisable(true);
		txtFieldArtistId.setDisable(true);
		
		txtFieldEmployeeId.setDisable(true);
		txtFieldFirstName.setDisable(true);
		txtFieldLastName.setDisable(true);
		txtFieldSecondTitle.setDisable(true);
	}
	
	public void makeEditableFields (){
		txtFieldAlbumId.setDisable(false);
		txtFieldTitle.setDisable(false);
		txtFieldArtistId.setDisable(false);
		
		txtFieldEmployeeId.setDisable(false);
		txtFieldFirstName.setDisable(false);
		txtFieldLastName.setDisable(false);
		txtFieldSecondTitle.setDisable(false);
	}

	}