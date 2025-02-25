# Documentation for REST API

This documentation describes the HTTP API operations currently supported by the backend.
Each operation is outlined with its purpose, URL, HTTP method,
and additional details like headers and body (if applicable).

---

## Create a Project

Creates a new project with a specified owner and name.

- **Method**: `POST`
- **URL**: `http://localhost:3000/project/new`
- **Headers**:
    - `Content-Type: application/json`
- **Response Codes**:
    - **200**: Successfully created project
    - **400**: Malformed body
    - **409**: Duplicate project
- **Body**:
    - Use JSON to specify the initial visibility of the project.
    - if `private` is `true` the project will not be visible

### Example

```http
POST http://localhost:3000/project/new
Content-Type: application/json

{
  "private": false,
  "ownerId": "AVeryCoolDude",
  "name": "MyCoolNewProject"
}
```

### Example Response

```json
{
  "private": false,
  "projectId": 211,
  "projectName": "MyCoolNewProject",
  "ownerId": "AVeryCoolDude",
  "slug": "my-cool-new-project"
}
```

---

## Rename a Project

Renames an existing project by its ID.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/project/rename/{projectId}/{name}`
- **Response Codes**:
    - **204**: Successfully renamed project
    - **400**: Malformed project id
    - **409**: Duplicate or unknown project
- **Query Parameters** (optional):
    - **ownerId**: only rename the project if it is owned by the specified user/org
- **Details**:
    - Replace `{projectId}` with the ID of the project to rename.
    - Replace `{name}` with the new name for the project.

### Example

- Rename project with id 1 by the specified user

  ```http
  PUT http://localhost:3000/project/rename/1/test test test?ownerId=test
  ```

- Rename project with id 1

  ```http
  PUT http://localhost:3000/project/rename/1/test test test
  ```

---

## Check Slug Availability

Checks if a project slug is available for use.

- **Method**: `HEAD`
- **URL**: `http://localhost:3000/project/slug/{slug}`
- **Response Codes**:
    - **204**: Slug is available
    - **409**: Slug is already in use
- **Details**:
    - Replace `{slug}` with the project slug to check

### Example

```http
HEAD http://localhost:3000/project/slug/my-cool-project
```

---

## **Update Project Icon**

Updates the icon for a specific project.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/project/settings/icon/{projectId}`
- **Headers**:
    - `Content-Type: application/json`
- **Response Codes**:
    - **204**: Successfully updated project icon
    - **304**: Project not found or no changes were made
    - **400**: Malformed project id or body
- **Query Parameters** (optional):
    - **ownerId**: only update the project if it is owned by the specified user/org
- **Body**:
    - Use JSON to specify the new icon for the project.

### Example

```http
PUT http://localhost:3000/project/settings/icon/1?ownerId=test
Content-Type: application/json

{
  "icon": "rocket"
}
```

---

## **Update Preview Chart Settings**

Updates the preview chart settings for a specific project.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/project/settings/preview/{projectId}`
- **Headers**:
    - `Content-Type: application/json`
- **Response Codes**:
    - **204**: Successfully updated preview chart settings
    - **304**: Project not found or no changes were made
    - **400**: Malformed project id or body
- **Query Parameters** (optional):
    - **ownerId**: only update the project if it is owned by the specified user/org
- **Body**:
    - Use JSON to specify the new settings for the preview chart.

### Example

```http
PUT http://localhost:3000/project/settings/preview/1?ownerId=test
Content-Type: application/json

{
  "chart": "total_servers"
}
```

---

## Update Project URL Settings

Updates the URL settings for a specific project.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/project/settings/url/{projectId}`
- **Headers**:
    - `Content-Type: application/json`
- **Response Codes**:
    - **204**: Successfully updated project URL settings
    - **304**: Project not found or no changes were made
    - **400**: Malformed project id or body
- **Query Parameters** (optional):
    - **ownerId**: only update the project if it is owned by the specified user/org
- **Body**:
    - Use JSON to specify the new URL settings for the project.

### Example

```http
PUT http://localhost:3000/project/settings/url/1?ownerId=test
Content-Type: application/json

{
  "url": "https://new-project-url.example.com"
}
```

---

## Update Visibility Settings

Updates the visibility settings for a specific project.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/project/settings/private/{projectId}/{private}`
- **Response Codes**:
    - **204**: Successfully updated visibility settings
    - **304**: Project not found or no changes were made
    - **400**: Malformed project id
- **Query Parameters** (optional):
    - **ownerId**: only update the project if it is owned by the specified user/org

### Example

- Update a project with id 1 by a specific user/org to private

  ```http
  PUT http://localhost:3000/project/settings/private/1/true?ownerId=test
  ```

- Update a project with id 1 to public

  ```http
  PUT http://localhost:3000/project/settings/private/1/false
  ```

---

## Update Project Slug

Updates the slug for a specific project.

- **Method**: `PUT`
- **URL**: `http://localhost:3000/project/settings/slug/{projectId}/{slug}`
- **Response Codes**:
    - **204**: Successfully updated project slug
    - **304**: Project not found or no changes were made
    - **400**: Malformed project id or slug
- **Query Parameters** (optional):
    - **ownerId**: only update the project if it is owned by the specified user/org
- **Slug Pattern**: `^(?=.{3,32}$)[a-z0-9]+(-[a-z0-9]+)*$`

### Example

- Update the slug of a project with id 1:

  ```http
  PUT http://localhost:3000/project/settings/slug/1/new-slug
  ```

- Update the slug of a project with id 1 for a specific owner:

  ```http
  PUT http://localhost:3000/project/settings/slug/1/new-slug?ownerId=test
  ```

## List Projects

Lists projects based on filters such as public/private visibility, user, and pagination.

- **Method**: `GET`
- **URL**: `http://localhost:3000/projects/list/{offset}/{limit}`
- **Response Codes**:
    - **200**: Successfully listed
    - **400**: Malformed offset, limit, or body
- **Query Parameters** (optional):
    - **publicOnly**: list projects by visibility
    - **ownerId**: list projects by a specific user/org
- **Details**:
    - If `publicOnly` is `true` only **public** projects will be listed,
      if `false` only **private** projects, and if omitted
      both **public and private** projects are listed
    - If `ownerId` is undefined projects of all users and orgs are listed
    - `offset` defines how many projects should be skipped (allowing pagination)
    - `limit` defines how many projects may be listed at max

### Examples

- List 20 public projects skipping the first 10:

  ```http
  GET http://localhost:3000/projects/list/10/20?publicOnly=true
  ```

- List 10 public projects by a specific user:

  ```http
  GET http://localhost:3000/projects/list/0/10?publicOnly=true&ownerId=testUser
  ```

- List 10 public and private projects by a specific user:

  ```http
  GET http://localhost:3000/projects/list/0/10?ownerId=testUser
  ```

- List 10 private projects by a specific user:

  ```http
  GET http://localhost:3000/projects/list/0/10?publicOnly=false&ownerId=testUser
  ```

### Example Response

```json
[
  {
    "private": false,
    "projectId": 1,
    "projectName": "MyCoolNewsProjects",
    "ownerId": "AVeryCoolDude",
    "project_url": "https://project.example.com",
    "icon": "gem",
    "slug": "cool-project"
  },
  {
    "private": true,
    "projectId": 4,
    "projectName": "test test test",
    "ownerId": "AVeryCoolDude",
    "icon": "white-flag",
    "slug": "test"
  }
]
```

---

## **Retrieve a Project**

Retrieve a project by its ID.

- **Method**: `GET`
- **URL**: `http://localhost:3000/project/{projectId}`
- **Response Codes**:
    - **200**: Successfully retrieved project
    - **400**: Malformed project id
    - **404**: Project not found
- **Query Parameters** (optional):
    - **ownerId**: retrieve the project only if it is owned by the specified user/org
- **Details**:
    - Replace `{projectId}` with the ID of the project to retrieve.

### Example

- Retrieve the project with id 1 if it is owned by a specific user

  ```http
  GET http://localhost:3000/project/1?ownerId=AVeryCoolDude
  ```

- Retrieve the project with id 1

  ```http
  GET http://localhost:3000/project/1
  ```  

### Example Response

```json
{
  "private": false,
  "projectId": 1,
  "projectName": "MyCoolNewsProjects",
  "ownerId": "AVeryCoolDude",
  "preview_chart": "total_servers",
  "project_url": "https://project.example.com",
  "icon": "gem",
  "slug": "cool-project",
  "layout": {
    "total_servers": {
      "name": "Total Servers",
      "type": "line_chart",
      "color": "#1da1f2"
    }
  }
}
```

---

## **Delete a Project**

Deletes a project by its ID.

- **Method**: `DELETE`
- **URL**: `http://localhost:3000/project/delete/{projectId}`
- **Response Codes**:
    - **204**: Successfully deleted project
    - **404**: Project not found
- **Query Parameters**
    - **ownerId**: only delete the project if it is owned by the specified user/org
- **Details**:
    - Replace `{projectId}` with the ID of the project to delete.

### Example

- Delete the project with id 1 if it is owned by a specific user

  ```http
  DELETE http://localhost:3000/project/delete/1?ownerId=AVeryCoolDude
  ```

- Delete the project with id 1

  ```http
  DELETE http://localhost:3000/project/delete/1
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