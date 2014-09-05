package textalytics.entity;

public class Student {
	private String name, project, roles;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Student(String name, String project, String roles) {
		super();
		this.name = name;
		this.project = project;
		this.roles = roles;
	}
}
