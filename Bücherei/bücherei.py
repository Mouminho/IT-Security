from pymongo import MongoClient
from werkzeug.security import generate_password_hash, check_password_hash
import logging
from cryptography.fernet import Fernet
import getpass
from bson.objectid import ObjectId

# MongoDB-Verbindung einrichten
client = MongoClient("mongodb://localhost:27017/")
db = client["library"]

# Logging für Audit-Logs konfigurieren
logging.basicConfig(filename='audit.log', level=logging.INFO)

# Schlüssel für die Datenverschlüsselung generieren
encryption_key = Fernet.generate_key()
cipher = Fernet(encryption_key)

# Benutzerrollen
roles = {
    "admin": "Administrator",
    "member": "Mitglied"
}

# Initiale Admin-Benutzeranmeldung
ADMIN_USERNAME = "Mouminho"
ADMIN_PASSWORD = "Asd123"

# Funktionen zur Benutzerverwaltung
def register_user(username, password, role):
    hashed_password = generate_password_hash(password)
    user_id = str(ObjectId())
    db.users.insert_one({
        "username": username,
        "password_hash": hashed_password,
        "role": role,
        "user_id": user_id  # Store the unique ID
    })
    logging.info(f"User {username} registered as {role} with ID {user_id}")

def authenticate_user(username, password):
    user = db.users.find_one({"username": username})
    if user and check_password_hash(user["password_hash"], password):
        logging.info(f"User {username} logged in")
        return user
    else:
        logging.warning(f"Failed login attempt for {username}")
        return None

def ensure_admin_exists():
    # Check if the admin user already exists
    if db.users.count_documents({"username": ADMIN_USERNAME}) == 0:
        hashed_password = generate_password_hash(ADMIN_PASSWORD)
        admin_user_id = str(ObjectId())  # Generate unique user_id for admin
        db.users.insert_one({
            "username": ADMIN_USERNAME,
            "password_hash": hashed_password,
            "role": "admin",
            "user_id": admin_user_id  # Add unique ID for the admin user
        })
        logging.info(f"Static admin user {ADMIN_USERNAME} created with ID {admin_user_id}")

def encrypt_data(data):
    return cipher.encrypt(data.encode())

def decrypt_data(encrypted_data):
    return cipher.decrypt(encrypted_data).decode()

def log_action(action):
    logging.info(action)

# Funktionen für Administratoren
def admin_actions():
    while True:
        print("\nAdmin-Bereich:")
        print("1. Benutzer hinzufügen")
        print("2. Alle Benutzer anzeigen")
        print("3. Benutzer löschen")
        print("4. Benutzerrolle ändern")  # New option to change user roles
        print("5. Abmelden")
        choice = input("Wählen Sie eine Aktion: ")

        if choice == "1":
            username = input("Benutzername: ")
            password = getpass.getpass("Passwort: ")
            role = "member"  # Standardmäßig als Mitglied
            register_user(username, password, role)
            print(f"Benutzer {username} hinzugefügt.")
        elif choice == "2":
            users = db.users.find()
            print("\nAlle Benutzer:")
            for user in users:
                print(f"Benutzer ID: {user['user_id']}, Benutzer: {user['username']}, Rolle: {user['role']}")
        elif choice == "3":
            user_id_to_delete = input("Benutzer ID des zu löschenden Benutzers: ")
            result = db.users.delete_one({"user_id": user_id_to_delete})
            if result.deleted_count > 0:
                print(f"Benutzer mit ID {user_id_to_delete} gelöscht.")
                logging.info(f"User with ID {user_id_to_delete} deleted by admin.")
            else:
                print(f"Benutzer mit ID {user_id_to_delete} nicht gefunden.")
        elif choice == "4":
            user_id_to_change = input("Benutzer ID der Rolle zu ändernden Benutzer: ")
            new_role = input("Neue Rolle (admin/member): ")
            if new_role in roles:
                result = db.users.update_one({"user_id": user_id_to_change}, {"$set": {"role": new_role}})
                if result.matched_count > 0:
                    print(f"Rolle von Benutzer ID {user_id_to_change} wurde zu {new_role} geändert.")
                    logging.info(f"Role of user with ID {user_id_to_change} changed to {new_role} by admin.")
                else:
                    print(f"Benutzer mit ID {user_id_to_change} nicht gefunden.")
            else:
                print("Ungültige Rolle! Bitte 'admin' oder 'member' wählen.")
        elif choice == "5":
            log_action("Admin logged out")
            break
        else:
            print("Ungültige Auswahl!")

# Funktionen für Mitglieder
def member_actions(username):
    while True:
        print("\nMitglieder-Bereich:")
        print("1. Daten anzeigen")
        print("2. Abmelden")
        choice = input("Wählen Sie eine Aktion: ")

        if choice == "1":
            user = db.users.find_one({"username": username})
            print(f"Benutzer ID: {user['user_id']}, Benutzer: {user['username']}, Rolle: {user['role']}")
        elif choice == "2":
            log_action(f"{username} logged out")
            break
        else:
            print("Ungültige Auswahl!")

# Hauptanwendung
def main():
    ensure_admin_exists()  # Ensure the static admin user exists

    while True:
        print("\nWillkommen im Bibliotheksverwaltungssystem")
        print("1. Anmelden")
        print("2. Registrieren")
        print("3. Beenden")
        choice = input("Wählen Sie eine Aktion: ")

        if choice == "1":
            username = input("Benutzername: ")
            password = getpass.getpass("Passwort: ")
            user = authenticate_user(username, password)
            if user:
                if user["role"] == "admin":
                    admin_actions()
                else:
                    member_actions(username)
            else:
                print("Anmeldung fehlgeschlagen!")
        elif choice == "2":
            username = input("Benutzername: ")
            password = getpass.getpass("Passwort: ")
            role = "member"  # Default role
            register_user(username, password, role)
            print(f"Benutzer {username} als {role} registriert.")
            input("Drücken Sie die Eingabetaste, um fortzufahren...")  # Wait for Enter to continue
        elif choice == "3":
            print("Programm beendet.")
            break  # This will exit the loop
        else:
            print("Ungültige Auswahl!")

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:  # Allow graceful exit with Ctrl + C
        print("\nProgramm wurde beendet.")
