# uparknow

![license](https://img.shields.io/badge/License-Viewing_Only_/_Proprietary_🔒-black)

> ⚠️ This repository is proprietary.  
> Viewing is permitted for evaluation purposes only.  
> Use, copy, modification, and redistribution are strictly prohibited.

# UParkNow

## About the Project
**UParkNow** is a mobile and web-based system developed as a Capstone Project at the **University of Utah**.  
It is designed to help students, faculty, and staff find available parking spaces on campus more efficiently.  

🚗 **Problem Motivation**: Parking at the University is highly competitive due to limited infrastructure, ongoing construction, and unpredictable events. Commuters often face long delays and stress trying to find parking near their destination.  

💡 **Our Solution**: UParkNow integrates **real-time camera-based parking detection** with a **human-based sharing system** (dubbed *“Jolly Car-Operation”*) to provide:  
- Automated identification of available spots through computer vision.  
- A social feature where users can share arrival/departure times to streamline spot handovers.  

## My Contribution
I (**Haiwei Yu**) was solely responsible for designing and implementing the **Backend System** of UParkNow.  

⚙️ **Backend Highlights**:  
- Built with **Java Spring Boot** (Java 17, Maven)  
- Deployed on **AWS EC2** (Free Tier)  
- Database: **MariaDB RDS**  
- Key features:  
  - Real-time communication via **WebSockets**  
  - User authentication and authorization (Spring Security)  
  - Mail service integration  
  - Data persistence with JPA  
  - REST API endpoints for Android and Manager frontend  
- Includes **16 automated tests** for reliability  

📄 **Design Document**: [Google Doc](https://docs.google.com/document/d/10v4jSwUiDiRH4QmV7CTgB23OkM36nE4BCTZFh9ugZcQ/edit?usp=sharing)  
🎥 **Demo Video**: [YouTube](https://youtu.be/GPugTEjIvZQ?si=4DNk5EiSLnjWOYiW)  

> ⚠️ Note: I only hold IP rights for the **backend** portion of this project.

## Contact
📬 This project was part of the University of Utah Capstone program.  
For backend-related inquiries or to review the full project details, please contact me.
