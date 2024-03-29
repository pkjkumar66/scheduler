### Booking scheduler website

It's a booking scheduler website for a car service agency where:

- Customer and service operator can register themselves.
- Customer can book, cancel or reschedule an appointments with service operators.

---

### Dependencies to run this appplication:

- [java developement IDE](https://www.jetbrains.com/idea/)
- [download homebrew](https://brew.sh/)
- [install java 17](https://formulae.brew.sh/formula/openjdk@17)
- [download docker](https://docs.docker.com/desktop/install/mac-install/) to run app/mysql instance
- [use this article to set up mysql instance in docker container](https://www.appsdeveloperblog.com/how-to-start-mysql-in-docker-container/)
- [use this to install mvn](https://formulae.brew.sh/formula/maven)

### To start the mysql db instance
- Step 1: [please check this article](https://www.appsdeveloperblog.com/how-to-start-mysql-in-docker-container/)
```
docker run -d -p 3306:3306 --name mysql-docker-container -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=pankaj -e MYSQL_USER=root -e MYSQL_PASSWORD=root mysql/mysql-server:latest
```
- Step 2: sudo docker exec -it mysql-docker-container bash [use your mac password]
- Step 3: mysql -u root -p [use password: root]
- Step 4: show databases;
- Step 5: use pankaj;

### To start the application
- Step 1: mvn clean install
- Step 2: java -jar appointment-booking-app-image.jar
- Step 3: acess rest apis using postman/local terminal

---
### How to use all APIs:
  [Postman Collection](https://red-water-686645.postman.co/workspace/My-Workspace~bfb5c795-ecc4-4e23-8ad9-7c7fe4b847b4/collection/25669291-569ed371-008e-4da7-a6ea-b426485b1466?action=share&creator=25669291)
- CustomerController
  - /add: to add customer
  - /{email}: to get customer info

- OperatorController
  - /add: to add service operator
  - /{email}: to get service operator info
  
- AppointmentController:
  - /book: to book appointment
  - /reschedule_or_cancel: to resheaudle or cancel appointment
  - /booked_slots: to get booked slots of a service operator
  - /open_slots: to get open slots of a service operator


### How to use these APIs to book an appointment:
- Step 1: add any cutomer and service operator
- Step 2: use api to book/reschedule/cancel appointment

---

### Tables:

- Customer: This table stores information about customers \
  (primary key:customer_id, unique constraint: email)
    - customer_id:
    - name
    - email
    - createdAt

- Operator: This table stores information about service operators \
  (primary key:operator_id, unique constraint: email)
    - operator_id:
    - name
    - email
    - createdAt

- Appointment: This table stores information about appointments \
  (primary key:appointment_id, constraint: customer_id, operator_id, startTime, endTime)
    - appointment_id:
    - customer_id
    - operator_id
    - startTime: meeting start timing
    - endTime: meeting end timing
    - Date
    - status

---

### Assumption:

- customer can book/reschedule appointment for exactly 1hr, and it is in this way only (1-2, 4-5, 6-7)
  i.e starting and ending interval must be integer and must have a difference of 1.
- customer can't book/reschedule appointment like (1.5 - 2.5, 4.15 - 5.15)
- I have chosen 24hr time format i.e if a customer wants to schedule an appointment for 3-4PM  
  then they have to choose (15-16)
- only registered customer and service operator can book, cancel or reschedule an appointment
- customer can book/reschedule appointment with a service operator, any number of times
  in a day but timing will be different

---

### Improvement:

- Error handling can be done better
- we will get status as 500 if above condition is not getting fulfilled so please see console msg
- unit/integration test can be added
- we can think of all the edge cases of the problem and implement it
- we can also support appointment like (1.5 - 2.5, 4.15 - 5.15)
- we can also add 12hr time format including an Enum like 12hr/24hr format

---
### References:

- If getting this error: \
  Error executing DDL "alter table operator add constraint operator_email_idx unique (email_id)"
  via JDBC Statement then we can change the property:
[stackoverflow](https://stackoverflow.com/questions/438146/what-are-the-possible-values-of-the-hibernate-hbm2ddl-auto-configuration-and-wha/1689769#1689769)

  - I have used: spring.jpa.hibernate.ddl-auto=create-drop
  - to fix above issue we can use: spring.jpa.hibernate.ddl-auto=update
  
- Error while datatbase setup: 
  - check database name
  - check root user name
  - check root user password









