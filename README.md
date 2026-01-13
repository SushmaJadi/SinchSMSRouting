Features
Carrier Routing: Routes messages to appropriate carriers based on phone number country codes
AU numbers → Telstra/Optus (random selection)
NZ numbers → Spark
All other countries → Global carrier (default)
Opt-out Management: Prevents sending messages to opted-out phone numbers
Message Status Tracking: Tracks message status
Assumptions
Current Scope and Limitations:

This application is designed as a message routing and validation service that handles the initial stages of SMS message processing. The system:

Routes messages to appropriate carriers based on phone number country codes
Validates phone numbers and checks opt-out status
Saves messages with initial status (PENDING for valid messages, BLOCKED for opted-out numbers)
Provides integration points for carrier service integration, but does not perform actual message delivery. As an assummption for the integration point, it provides webhook for updating status to DELIVERED.
What the system does NOT do:
Quick Start
Prerequisites
Java 17 or higher
Maven (or use the included Maven wrapper)
Build the Application
# Clean and build the project
./mvnw clean install
Run the Application
# Run using Maven Spring Boot plugin
./mvnw spring-boot:run
The application will start on http://localhost:8080 by default.

API Endpoints
Send SMS Message
POST /messages

Send an SMS message to a phone number.

Request Body:
Actual message delivery to end users
Status updates to SENT or DELIVERED which require integration with carrier services. Currently there are extensible and helper integration points, e.g. onMessagePending hook method, updateSentStatus and updateDeliveredStatus methods in MessageService), and a webhook API to update status to DELIVERED.
{
"messageBody":"Hi hello world",
"senderPhoneNumber":"+61412345649",
"receiverPhoneNumber":"+6112345673"

}
Response(Success- 200 ok):
{
"id": 1,
"messageBody":"Hi hello world",
"senderPhoneNumber":"+61412345649",
"receiverPhoneNumber":"+6112345673",
"areaCode": Autstralia,
"carrier": "TELSTRA",
"status": "PENDING",
}
Response (Success - 201 Created):

{
"messageId": 1,
"status": "PENDING"
}
Response (Opted-out - 403 Forbidden):

Cannot send message to this phone number as it is opted out
Response (Invalid Phone - 400 Bad Request):

Invalid phone number

Get All Messages (testing purpose)
GET /messages

Get all messages in the system.

Response (200 OK):

[
{
"id": 1,
"messageBody":"Hi hello world",
"senderPhoneNumber":"+61412345649",
"receiverPhoneNumber":"+6112345673",
"areaCode": Autstralia,
"carrier": "TELSTRA",
"status": "PENDING",
}
]

Opt-out Phone Number
POST /optout/{phoneNumber}

Opt-out a phone number from receiving messages.

Response (201 Created):

{
"id": 1,
"phoneNumber": "+61487654321"
}
Response (400 Bad Request - Already opted out):

Phone number already opted out
Remove Opt-out
DELETE /optout/{phoneNumber}

Remove a phone number from the opt-out list.

Response (200 OK):

1
Response (400 Bad Request - Not opted out):

Phone number cannot be opted in as it is not opted out
GET /optout/{phoneNumber}

find a phone number from the opt-out list.
Response (200 OK):
{
"id": 1,
"phoneNumber": "+61487654321"
}
if not found
404 - Number not found 

