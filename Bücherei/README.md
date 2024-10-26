# Library System Simulation with IT Security 

This project simulates a library system (Bücherei) with a focus on managing user identities and enhancing IT security. The system uses MongoDB to store user information.

## Table of Contents
- [Features](#features)
- [Usage](#usage)
- [Code Explanation](#code-explanation)

## Features
- Connects to a MongoDB database to manage library users.
- Automatically assigns a unique `user_id` to users who do not have one.
- Updates existing users without a `user_id` and logs the changes.

### Prerequisites
- Python 3.x
- MongoDB installed and running locally (default port: 27017)


## Usage

1. Run the script:
   ```bash
   python <script-name>.py
   ```

2. The script will connect to your MongoDB instance, search for users without a `user_id`, and assign each a unique ID. It will print out confirmation messages for each user updated.


## Code Explanation

- **MongoDB Connection**: 
  - The script uses `MongoClient` from the `pymongo` library to establish a connection to the local MongoDB instance.

- **Finding Users**:
  - It queries the `users` collection for documents that do not have a `user_id` field.

- **Generating Unique IDs**:
  - For each user found, a new unique `user_id` is generated using `ObjectId()`.

- **Updating the Database**:
  - The script updates each user document to include the new `user_id` and logs the operation to the console.


