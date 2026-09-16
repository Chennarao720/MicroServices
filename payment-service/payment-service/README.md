# Payment Service
Simple demo Payment Microservice for the Order -> Payment flow. No real payment gateway.

Port: 8083
Eureka: http://localhost:8761/eureka/
Database: paymentmicroservice

POST http://localhost:8083/payment
Request: { "orderId": 10, "amount": 2000 }
Response: { "paymentId": 1, "orderId": 10, "amount": 2000, "status": "SUCCESS" }
