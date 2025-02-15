# Documentation for REST API

This documentation describes the HTTP API operations currently supported by the backend.
Each operation is outlined with its purpose, URL, HTTP method,
and additional details like headers and body (if applicable).

---

## Create a Project

Creates a new project with a specified owner and name.

- **Method**: `POST`
- **URL**: `http://localhost:3000/projects/{owner}/{projectName}`
- **Headers**:
    - `Content-Type: application/json`
- **Response Codes**:
    - **200**: Successfully created project
    - **409**: Duplicate project
- **Details**:
    - Replace `{owner}` with the username of the project owner.
    - Replace `{projectName}` with the desired project name.
- **Body**:
    - Use JSON to specify the initial visibility of the project.
    - if `private` is `true` the project will not be visible

### Example

```http
POST http://localhost:3000/projects/AVeryCoolDude/MyCoolNewProject
Content-Type: application/json

{
  "private": false
}
```

### Example Response

```json
{
  "private": false,
  "projectId": 211,
  "projectName": "MyCoolNewProject",
  "userId": "AVeryCoolDude"
}
```

---

## Rename a Project

Renames an existing project by its ID.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/projects/{projectId}/{newName}`
- **Response Codes**:
    - **200**: Successfully renamed project
    - **304**: Name didn't change
    - **400**: Malformed project id
    - **404**: Project not found
    - **409**: Duplicate project
- **Details**:
    - Replace `{projectId}` with the ID of the project to rename.
    - Replace `{newName}` with the new name for the project.

### Example

```http
POST http://localhost:3000/projects/1/test test test
```

---

## Update Project Settings

Updates the settings for a project, such as visibility and layout configurations.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/projects/{projectId}`
- **Headers**:
    - `Content-Type: application/json`
- **Response Codes**:
    - **200**: Successfully updated project
    - **304**: Settings didn't change
    - **400**: Malformed project id or body
    - **404**: Project not found
- **Body**:
    - Use JSON to specify settings like `private` visibility and the layout configuration of charts.

### Example

> [!IMPORTANT]
> This route is not finished yet<br>
> the `private` setting does work but `layout` doesn't

```http
PUT http://localhost:3000/projects/1
Content-Type: application/json

{
 "private": true,
 "layout": {
   "total_servers": {
     "name": "Total Servers",
     "type": "line_chart",
     "color": "#1da1f2"
   },
   "total_players": {
     "name": "Total Players",
     "type": "line_chart",
     "color": "#1da1f2"
   },
   "online_mode": {
     "name": "Online Mode",
     "type": "pie_chart",
     "color": "#ffffff"
   },
   "server_software": {
     "name": "Server Software",
     "type": "pie_chart",
     "color": "#123456"
   }
 }
}
```

---

## List Projects

Lists projects based on filters such as public/private visibility, user, and pagination.

- **Method**: `GET`
- **URL**: `http://localhost:3000/projects/{offset}/{limit}`
- **Headers**:
    - `Content-Type: application/json` (optional, required for filtering based on project visibility and user).
- **Response Codes**:
    - **200**: Successfully listed
    - **400**: Malformed offset, limit, or body
- **Body** (optional):
    - Specify filters in JSON (`publicOnly`, `userId`).
- **Details**:
    - If `publicOnly` is `true` only **public** projects will be listed,
      if `false` only **private** projects, and if omitted
      both **public and private** projects are listed
    - If `userId` is undefined projects of all users are be listed
    - `offset` defines how many projects should be skipped (allowing pagination)
    - `limit` defines how many projects may be listed at max

### Examples

- List 20 public projects skipping the first 10:

  ```http
  GET http://localhost:3000/projects/10/20?publicOnly=true
  ```

- List 10 public projects by a specific user:

  ```http
  GET http://localhost:3000/projects/0/10?publicOnly=true&userId=testUser
  ```

- List 10 public and private projects by a specific user:

  ```http
  GET http://localhost:3000/projects/0/10?userId=testUser
  ```

- List 10 private projects by a specific user:

  ```http
  GET http://localhost:3000/projects/0/10?publicOnly=false&userId=testUser
  ```

### Example Response

```json
[
  {
    "private": false,
    "projectId": 1,
    "projectName": "MyCoolNewsProjects",
    "userId": "AVeryCoolDude"
  },
  {
    "private": true,
    "projectId": 4,
    "projectName": "test test test",
    "userId": "AVeryCoolDude"
  }
]
```

---

## 5. **Delete a Project**

Deletes a project by its ID.

- **Method**: `DELETE`
- **URL**: `http://localhost:3000/projects/{projectId}`
- **Response Codes**:
    - **200**: Successfully deleted project
    - **404**: Project not found
- **Details**:
    - Replace `{projectId}` with the ID of the project to delete.

### Example

```http
DELETE http://localhost:3000/projects/1
```

---

# Running the Project Using Docker

To run the project with Docker, follow these steps:

1. **Set up the Environment**:

    - Ensure you have Docker and Docker Compose installed on your system.
    - If not, refer to the official [Docker Installation Guide](https://docs.docker.com/get-docker/)
      and [Docker Compose Installation Guide](https://docs.docker.com/compose/install/).

2. **Start the Services**:

    - Use the provided `docker-compose.yml` file to spin up the necessary containers (backend and MongoDB).
    - Run the following command in the directory where the `docker-compose.yml` file is located:

   ```bash
   docker-compose up --build
   ```

3. **Access the Application**:

    - The backend service will be accessible at **http://localhost:3000**.
    - MongoDB is configured as a dependency and will automatically be linked to the backend container.

4. **Environment Variables**:

    - The `MONGODB_URL` environment variable is set to `mongodb://user:password@mongodb:27017/`.
    - The backend server will run on port `3000`, mapped to the host machine.

5. **Persistent Data**:

    - The MongoDB data is stored in a Docker volume (`mongodb`) to ensure persistence across restarts.

6. **Stop the Services**:

    - To stop the running containers, press `CTRL+C` or run:

   ```bash
   docker-compose down
   ```

7. **Restart Containers**:

    - If you’ve made changes to the code or configuration, restart the containers using:

   ```bash
   docker-compose up --build
   ```

8. **Health Check**:

    - The MongoDB service includes a `healthcheck` to verify that the database
      is ready and responsive before the backend service starts.
    - You can verify container health by running:

   ```bash
   docker ps
   ```

This setup ensures the backend and MongoDB are easily started, linked, and maintained using Docker Compose.