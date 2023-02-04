### Booking scheduler website

It's a booking scheduler website for a car service agency where:

- Customer and service operator can register themselves.
- Customer can book, cancel or reschedule an appointments with service operators.

---


### How to Run This APP:
- download homebrew:
- download docker to run app/mysql instance
- use this command to get mysql instance: docker pull mysql/mysql-server:latest
- use this command to run mysql instance:

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
- we will get status as 500 if above condition is not getting fulfilled
- unit/integration test can be added
- we can also support appointment like (1.5 - 2.5, 4.15 - 5.15)
- we can also add 12hr time format including an Enum like 12hr/24hr format

---











