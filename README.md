# LagAlt - Backend

This project is the backend part of the lagalt project. This project is a REST AP which is connected to a postgres database. 

## Team members
- Camilla Felin
- Thea Thodesen
- Bjørnar Pedersen
- Thomas Gulli

## Heroku page

https://lagalt-service.herokuapp.com

## Swagger

## About the lagalt backend api
It is a REST API for communication with the postgres database. The database stores users and projects, and their information.


## Functionality

### Login controller

- Post mapping (https://lagalt-service.herokuapp.com/api/v1/public/login/internal)

     Creates an authentication token, if the username and password is correct, and the user account is verified.

   
- Post mapping (https://lagalt-service.herokuapp.com/api/v1/public/login/facebook/{accessToken})
  
    Creates a new authentication token from the facebook token. Creates a new user, if the email address is not registered in the database. Else, it creates an token for the existing user with the email address.


- Postmapping (https://lagalt-service.herokuapp.com/api/v1/public/login/google/{accessToken})

  Creates a new authentication token from the google token. Creates a new user, if the email address is not registered in the database. Else, it creates an token for the existing user with the email address.
   


### Public project controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/public/projects/{id})
  
    Gets project details based on the project ID from parameter. The returned project details are:
  - Name
  - Description
  - Category
  - Progress
  - Owners username
  - Tags
    

### Public user controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/public/users/{id})
  
    Gets user details based on the ID from parameter. The returned user details are:
    - Username
    - Name
    - Biography
    

### Register controller

- Post mapping (https://lagalt-service.herokuapp.com/api/v1/public/register)
  
    Creates a user with a user as requestbody. When register a new user, following data is required:
  - Name 
  - Username 
  - Password 
  - Email address


- https://lagalt-service.herokuapp.com/api/v1/public/confirm-account
  Confirms a user with a given token, if the token is valid, the corresponding user is set to verified.
  It also sends a redirect link for login.
    
### User controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/users/{id})

  Gets a user with the given id from parameter if the token correspondence to the user id.


- Delete mapping (https://lagalt-service.herokuapp.com/api/v1/users/{id})

  Deletes a user with the given id from parameter if the token correspondence to the user id. Deletes all of its corresponding data in other tables. Requires a bearer token in body.


- Put mapping (https://lagalt-service.herokuapp.com/api/v1/users/{id})

  Updates a user with the given id from parameter if the token correspondence to the user id.  Requires a bearer token in body, and takes the new user data in body too.



### User tags controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/usertags)

  Gets all tags stored in users.


- Get mapping (https://lagalt-service.herokuapp.com/api/v1/alltags)

  Gets all tags stored in users and projects.


- Post mapping (https://lagalt-service.herokuapp.com/api/v1/alltags)

  Posts a new usertag, with the given usertag in requestbody,

### Project controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/projects/{id})

  Gets a project with the given id from parameter. Requires a bearer token in body.


- Post mapping (https://lagalt-service.herokuapp.com/api/v1/projects/)

  Posts a new project from requestbody. Requires a bearer token in body.


- Get mapping (https://lagalt-service.herokuapp.com/api/v1/public/projects/show/{page})

  Gets the project based on a given site from paramter.


- Delete mapping (https://lagalt-service.herokuapp.com/api/v1/projects/{id})

  Deletes a project with the given id from parameter if the user is the owner of the project. Deletes all of its corresponding data in other tables. Requires a bearer token in body.


- Put mapping (https://lagalt-service.herokuapp.com/api/v1/projects/{id})

  Updates a project with the given id from parameter if the user is the owner of the project. Deletes all of its corresponding data in other tables. Requires a bearer token in body.

### Project tags controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/projecttags)

  Gets all tags stored in projects.


- Post mapping (https://lagalt-service.herokuapp.com/api/v1/projecttags)

  Posts a new projecttag, with the given projecttag in requestbody,

### Project collaborators controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/project/collaborators/{id})

  Gets a project collaborator based on id.


- Get mapping (https://lagalt-service.herokuapp.com/api/v1/project/{id}/collaborators)

  Gets all project collaborators based on the project id.


- Post mapping (https://lagalt-service.herokuapp.com/api/v1/project/collaborators)

  Posts a new project collaborator.


- Put mapping (https://lagalt-service.herokuapp.com/api/v1/project/collaborators/{id})

  Updates a project collaborator with a given id.

### Message controller

- Get mapping (https://lagalt-service.herokuapp.com/api/v1/messages/project/{id}/user/{userid})

  Gets all messages for a given project.


- Post mapping (https://lagalt-service.herokuapp.com/api/v1/messages)

  Post a message.


- Put mapping (https://lagalt-service.herokuapp.com/api/v1/messages/{id})

  Edits a message with the id.

### Chat controller

- Message mapping (https://lagalt-service.herokuapp.com/api/v1/chat.sendMessage)

  Sends a new chat message.


- Message mapping (https://lagalt-service.herokuapp.com/api/v1/chat.addUser)

  Adds a user to the message


- Get mapping (https://lagalt-service.herokuapp.com/api/v1/project/{id}/user/{userId})
  
  Gets the messages for the given project for the given user.

## Technologies

- Java
- Spring
- REST API

## Motivation

This project was made as a case assignment during the Experis Academy Java Fullstack course.

## Project tree

## Credits

Thanks to our mentor Steven James Delton for great guidance and discussions.
