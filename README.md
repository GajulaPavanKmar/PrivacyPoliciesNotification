Description
This project develops a notification service that monitors changes in the privacy policies of specified websites and alerts users about these changes. The primary goal is to enhance digital transparency and empower users by keeping them informed about how their personal data is being managed.

Installation
Clone the repository:
git clone https://github.com/GajulaPavanKmar/PrivacyPoliciesNotification.git
Navigate to the project directory:
cd PrivacyPoliciesNotification
Install dependencies (ensure Java and Maven are installed):
mvn install
Usage
Start the application:
java -jar target/PrivacyPoliciesNotification-0.1.0.jar
Open your web browser and go to http://localhost:8080 to access the application.
Register and log in to set up your monitoring preferences for desired websites.

Features
Web Scraping: Automatically scrapes privacy policies from specified websites.
Change Detection: Detects changes in the text of privacy policies using advanced algorithms.
Email Notifications: Sends email alerts to users when changes are detected in the monitored privacy policies.
