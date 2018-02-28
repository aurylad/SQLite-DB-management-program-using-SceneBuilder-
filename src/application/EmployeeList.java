package application;

public class EmployeeList {
	private int employeeId;
	private String lastName;
	private String firstName;
	private String secondTitle;
	
	public EmployeeList(int employeeId, String lastName, String firstName, String secondTitle) {
		this.employeeId = employeeId;
		this.lastName = lastName;
		this.firstName = firstName;
		this.secondTitle = secondTitle;
	}

	public String getSecondTitle() {
		return secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	
}
