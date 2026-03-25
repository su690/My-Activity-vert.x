🔐 Auth Service
Method	URL	Body
POST	http://localhost:8080/api/auth/register	{"email":"test@test.com","password":"pass123","name":"Test User"}
POST	http://localhost:8080/api/auth/login	{"email":"admin@activity.com","password":"admin123"}
GET	http://localhost:8080/api/auth/validate	Header: Authorization: Bearer <token>
👤 User Service
Method	URL
GET	http://localhost:8080/api/users
GET	http://localhost:8080/api/users/:id
POST	http://localhost:8080/api/users
PUT	http://localhost:8080/api/users/:id
DELETE	http://localhost:8080/api/users/:id
POST body:


{"userId":"<uuid>","email":"user@test.com","name":"Jane","phone":"9999999999"}
🏃 Activity Service
Method	URL
GET	http://localhost:8080/api/activities
GET	http://localhost:8080/api/activities/:id
POST	http://localhost:8080/api/activities
PUT	http://localhost:8080/api/activities/:id
DELETE	http://localhost:8080/api/activities/:id
POST body:


{
  "name": "Morning Run",
  "category": "Fitness",
  "description": "Early morning jogging session",
  "duration": 45,
  "location": "City Park",
  "maxParticipants": 20
}
📅 Planner Service
Method	URL
GET	http://localhost:8080/api/plans
GET	http://localhost:8080/api/plans/:id
GET	http://localhost:8080/api/plans/user/:userId
POST	http://localhost:8080/api/plans
PUT	http://localhost:8080/api/plans/:id
DELETE	http://localhost:8080/api/plans/:id
POST body:


{
  "userId": "<user-id-from-login>",
  "activityId": "<activity-id>",
  "activityName": "Morning Run",
  "scheduledDate": "2026-04-01",
  "scheduledTime": "07:00:00",
  "notes": "Bring water bottle"
}
🔔 Notification Service
Method	URL
GET	http://localhost:8080/api/notifications/user/:userId
PUT	http://localhost:8080/api/notifications/:id/read
🩺 Health Checks (direct service ports)
Service	URL
API Gateway	http://localhost:8080/health
Auth	http://localhost:8081/health
User	http://localhost:8082/health
Activity	http://localhost:8083/health
Planner	http://localhost:8084/health
Notification	http://localhost:8085/health
Postman tip: After calling /api/auth/login, copy the token from the response and set it as a collection variable. Then use {{token}} in the Authorization: Bearer {{token}} header for all protected requests.
