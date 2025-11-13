# **Blog REST API**

This project is a Spring Boot application that implements a robust, secure RESTful API for a simple blog platform. It features full CRUD operations for posts, user authentication using JWT (JSON Web Tokens), and user management.

## **🚀 Key Features**

* **JWT Authentication:** Secure user login and registration endpoints.  
* **Post Management:** Create, read, update, and delete posts (CRUD).  
* **Authorization:** Posts can only be edited or deleted by their original author.  
* **Real-time Pagination:** Efficient handling of large datasets for post listings and user searches using Spring Data JPA.  
* **API Documentation (Swagger/OpenAPI):** Self-documenting API accessible via a browser.

## **🛠️ Technology Stack**

* **Backend:** Java 19+  
* **Framework:** Spring Boot 3+  
* **Data:** Spring Data JPA (Relational Database)  
* **Security:** Spring Security & jjwt (JSON Web Tokens)  
* **Build Tool:** Maven

## **💻 Getting Started**

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### **Prerequisites**

You need to have the following installed:

* Java Development Kit (JDK) 19 or higher  
* Maven (or just use the provided mvnw wrapper)

### **Setup and Running**

1. **Clone the repository:**  
   ```
   git clone https://github.com/detectivedots/blog.git
   cd blog
   ```
2. Build the project:  
   Use the Maven Wrapper (mvnw) to compile the source code and package the application.  
   ```
   ./mvnw clean install
   ```

3. Run the application:  
   Start the Spring Boot application from the command line. This will typically run on http://localhost:8080.  
   ```
   ./mvnw spring-boot:run
   ```

## **🗺️ API Endpoints & Documentation**

Once the application is running, the full API documentation, built using SpringDoc OpenAPI, is available at the following URL. This interface allows you to view all endpoints, model schemas, and **execute requests directly after providing a JWT in the global authorization field.**

**Swagger UI Link:**

http://localhost:8080/swagger-ui/index.html

### **Authentication Flow**

1. **Register:** POST /api/auth/register  
2. **Login:** POST /api/auth/login (Returns a JWT).  
3. **Authorize:** Paste the returned JWT into the Swagger global "Authorize" button using the format Bearer \<YOUR\_TOKEN\>.

### **Endpoint Summary**

| Controller | Method | Path | Description |
| :---- | :---- | :---- | :---- |
| **Authentication** | POST | /api/auth/register | Create a new user account. |
|  | POST | /api/auth/login | Authenticate and receive a JWT. |
| **Posts** | GET | /api/posts/find\_all | Get all posts (Paginated, sorted by recent date by default). |
|  | GET | /api/posts/{id} | Get a single post by ID. |
|  | POST | /api/posts/create | Create a new post (Requires JWT). |
|  | PUT | /api/posts/edit | Update an existing post (Requires JWT & authorship). |
|  | DELETE | /api/posts/{id} | Delete a post by ID (Requires JWT & authorship). |
|  | GET | /api/posts/find\_by\_user/{user\_id} | Get all posts by a specific user (Paginated). |
| **Users** | GET | /api/user/{id} | Get user details by ID. |
|  | GET | /api/user/search | Search users by username (Paginated). |

